package MySecondTestAppSpringBootLab3.service;

import org.springframework.stereotype.Service;
import MySecondTestAppSpringBootLab3.model.Positions;

import java.time.Year;

@Service
public class AnnualBonusServiceImpl implements AnnualBonusService {

    @Override
    public double calculate(Positions positions, double salary, double bonus, int workDays) {
        int daysInYear =    Year.now().isLeap() ? 366 : 365;
        return salary * bonus * daysInYear * positions.getPositionCoefficient() / workDays;
    }

    @Override
    public double calculateQuarterlyBonus(Positions positions, double salary, double bonus) {
        if (!positions.isManager()) {
            return 0;
        }
        return salary * bonus * positions.getPositionCoefficient() / 4;
    }
}
