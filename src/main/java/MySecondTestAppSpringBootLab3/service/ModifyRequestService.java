package MySecondTestAppSpringBootLab3.service;

import org.springframework.stereotype.Service;
import MySecondTestAppSpringBootLab3.model.Request;

@Service
public interface ModifyRequestService {

    void modify(Request request);

}
