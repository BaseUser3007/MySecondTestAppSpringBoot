package MyThirdTestAppSpringBoot.service;

import org.springframework.stereotype.Service;
import MyThirdTestAppSpringBoot.model.Response;

@Service
public interface ModifyResponseService {

    Response modify(Response response);

}
