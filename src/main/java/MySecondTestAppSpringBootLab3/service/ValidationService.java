package MySecondTestAppSpringBootLab3.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import MySecondTestAppSpringBootLab3.exception.ValidationFailedException;

@Service
public interface ValidationService {
    void isValid(BindingResult bindingResult) throws ValidationFailedException;
}
