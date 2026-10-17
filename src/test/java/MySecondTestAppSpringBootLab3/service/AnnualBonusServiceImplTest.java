package MySecondTestAppSpringBootLab3.service;

import MySecondTestAppSpringBootLab3.model.Positions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AnnualBonusServiceImplTest {

    @Test
    void calculate() {

        // given
        Positions position = Positions.HR;
        double bonus = 2.0;
        int workDays = 243;
        double salary = 100000.00;

        // when
        double result = new AnnualBonusServiceImpl().calculate(position, salary, bonus, workDays);

        // then
        double expected = 360493.8271604938;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void calculateQuarterlyBonusForManager() {

        // given
        Positions position = Positions.TL;
        double bonus = 2.0;
        double salary = 100000.00;

        // when
        double result = new AnnualBonusServiceImpl().calculateQuarterlyBonus(position, salary, bonus);

        // then
        double expected = 130000.0;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void calculateQuarterlyBonusForNotManager() {

        // given
        Positions position = Positions.DEV;
        double bonus = 2.0;
        double salary = 100000.00;

        // when
        double result = new AnnualBonusServiceImpl().calculateQuarterlyBonus(position, salary, bonus);

        // then
        double expected = 0.0;
        assertThat(result).isEqualTo(expected);
    }
}