package MySecondTestAppSpringBootLab3.controller;

import MySecondTestAppSpringBootLab3.model.*;
import MySecondTestAppSpringBootLab3.service.AnnualBonusService;
import MySecondTestAppSpringBootLab3.service.ModifyRequestService;
import MySecondTestAppSpringBootLab3.service.ModifyResponseService;
import MySecondTestAppSpringBootLab3.util.DateTimeUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import MySecondTestAppSpringBootLab3.exception.UnsupportedCodeException;
import MySecondTestAppSpringBootLab3.exception.ValidationFailedException;
import MySecondTestAppSpringBootLab3.service.ValidationService;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestController @Slf4j
public class MyController {

    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;
    private final ModifyRequestService modifyRequestService;
    private final ModifyRequestService modifySourceRequestService;
    private final AnnualBonusService annualBonusService;

    @Autowired
    public MyController(ValidationService validationService,
                        @Qualifier("ModifySystemTimeResponseService") ModifyResponseService modifyResponseService,
                        @Qualifier("ModifySystemNameRequestService") ModifyRequestService modifyRequestService,
                        @Qualifier("ModifySourceRequestService") ModifyRequestService modifySourceRequestService,
                        AnnualBonusService annualBonusService) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
        this.modifyRequestService = modifyRequestService;
        this.modifySourceRequestService = modifySourceRequestService;
        this.annualBonusService = annualBonusService;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                             BindingResult bindingResult) {

        String receivedTime = DateTimeUtil.getCustomFormat().format(new Date());
        log.info("request: {}", request);

        Response response = Response.builder()
                .uid(request.getUid())
                .operationUid(request.getOperationUid())
                .systemTime(DateTimeUtil.getCustomFormat().format(new Date()))
                .code(Codes.SUCCESS)
                .errorCode(ErrorCodes.EMPTY)
                .errorMessage(ErrorMessages.EMPTY)
                .build();
        log.info("response создан: {}", response);

        try {
            validationService.isValid(bindingResult);
            if (request.getUid().equals("123")) {
                log.error("uid = 123, выбрасывается UnsupportedCodeException");
                throw new UnsupportedCodeException("uid 123 не поддерживается");
            }
            response.setAnnualBonus(annualBonusService.calculate(request.getPosition(),
                    request.getSalary(), request.getBonus(), request.getWorkDays()));
            response.setQuarterlyBonus(annualBonusService.calculateQuarterlyBonus(request.getPosition(),
                    request.getSalary(), request.getBonus()));
            log.info("в response записаны премии: {}", response);
        } catch (ValidationFailedException e) {
            log.error("Поймано ValidationFailedException: {}", e.getMessage());
            return errorResponse(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST);
        } catch (UnsupportedCodeException e) {
            log.error("Поймано UnsupportedCodeException: {}", e.getMessage());
            return errorResponse(response, ErrorCodes.UNSUPPORTED_EXCEPTION, ErrorMessages.UNSUPPORTED, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Поймано непредвиденное исключение: {}", e.getMessage());
            return errorResponse(response, ErrorCodes.UNKNOWN_EXCEPTION, ErrorMessages.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("итоговый response: {}", response);

        request.setSystemTime(receivedTime);
        log.info("в request.systemTime записано время получения запроса Сервисом 1: {}", receivedTime);

        modifySourceRequestService.modify(request);

        modifyRequestService.modify(request);

        return new ResponseEntity<>(modifyResponseService.modify(response), HttpStatus.OK);
    }

    private ResponseEntity<Response> errorResponse(Response response, ErrorCodes errorCode,
                                                   ErrorMessages errorMessage, HttpStatus status) {
        response.setCode(Codes.FAILED);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        log.info("response после ошибки: {}", response);
        return new ResponseEntity<>(response, status);
    }
}
