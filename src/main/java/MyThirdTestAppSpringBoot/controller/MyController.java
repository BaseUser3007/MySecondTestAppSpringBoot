package MyThirdTestAppSpringBoot.controller;

import MyThirdTestAppSpringBoot.model.*;
import MyThirdTestAppSpringBoot.service.ModifyResponseService;
import MyThirdTestAppSpringBoot.util.DateTimeUtil;
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
import MyThirdTestAppSpringBoot.exception.UnsupportedCodeException;
import MyThirdTestAppSpringBoot.exception.ValidationFailedException;
import MyThirdTestAppSpringBoot.service.ValidationService;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestController @Slf4j
public class MyController {

    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;

    @Autowired
    public MyController(ValidationService validationService,
                        @Qualifier("ModifySystemTimeResponseService") ModifyResponseService modifyResponseService) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                             BindingResult bindingResult) {

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
            Date requestTime = DateTimeUtil.getCustomFormat().parse(request.getSystemTime());
            long difference = new Date().getTime() - requestTime.getTime();
            log.info("Время от получения request Сервисом 1 до получения Сервисом 2: {} мс", difference);
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

        return new ResponseEntity<>(modifyResponseService.modify(response), HttpStatus.OK);
    }
}
