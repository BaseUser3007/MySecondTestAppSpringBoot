package MySecondTestAppSpringBootLab3.controller;

import MySecondTestAppSpringBootLab3.model.*;
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

    @Autowired
    public MyController(ValidationService validationService,
                        @Qualifier("ModifySystemTimeResponseService") ModifyResponseService modifyResponseService,
                        @Qualifier("ModifySystemNameRequestService") ModifyRequestService modifyRequestService,
                        @Qualifier("ModifySourceRequestService") ModifyRequestService modifySourceRequestService) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
        this.modifyRequestService = modifyRequestService;
        this.modifySourceRequestService = modifySourceRequestService;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                             BindingResult bindingResult) {

        String receivedTime = DateTimeUtil.getCustomFormat().format(new Date());
        log.info("request: {}", request);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
        } catch (ValidationFailedException e) {
            log.error("Поймано ValidationFailedException: {}", e.getMessage());
            response.setCode(Codes.FAILED);
            response.setErrorCode(ErrorCodes.VALIDATION_EXCEPTION);
            response.setErrorMessage(ErrorMessages.VALIDATION);
            log.info("response после ошибки валидации: {}", response);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (UnsupportedCodeException e) {
            log.error("Поймано UnsupportedCodeException: {}", e.getMessage());
            response.setCode(Codes.FAILED);
            response.setErrorCode(ErrorCodes.UNSUPPORTED_EXCEPTION);
            response.setErrorMessage(ErrorMessages.UNSUPPORTED);
            log.info("response после UnsupportedCodeException: {}", response);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Поймано непредвиденное исключение: {}", e.getMessage());
            response.setCode(Codes.FAILED);
            response.setErrorCode(ErrorCodes.UNKNOWN_EXCEPTION);
            response.setErrorMessage(ErrorMessages.UNKNOWN);
            log.info("response после непредвиденной ошибки: {}", response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        modifyResponseService.modify(response);
        log.info("итоговый response: {}", response);

        request.setSystemTime(receivedTime);
        log.info("в request.systemTime записано время получения запроса Сервисом 1: {}", receivedTime);

        modifySourceRequestService.modify(request);

        modifyRequestService.modify(request);

        return new ResponseEntity<>(modifyResponseService.modify(response), HttpStatus.OK);
    }
}
