package MySecondTestAppSpringBootLab3.service;

import org.springframework.stereotype.Service;
import MySecondTestAppSpringBootLab3.model.Positions;

@Service
public interface AnnualBonusService {

    double calculate(Positions positions, double salary, double bonus, int workDays);
    double calculateQuarterlyBonus(Positions positions, double salary, double bonus);

}