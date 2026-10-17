package MySecondTestAppSpringBootLab3.service;

import org.springframework.stereotype.Service;
import MySecondTestAppSpringBootLab3.model.Response;

@Service
public interface ModifyResponseService {

    Response modify(Response response);

}
