package decathlonTest;

import com.example.decathlon.common.CalcTrackAndField;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecaTest {

    private double constA, constB, constC, result;

    @Test
    public void testDecaValidInput100M() {
        CalcTrackAndField calc = new CalcTrackAndField();

        constA = 25.4347;
        constB = 18;
        constC = 1.81;
        result = 10;

        int actual = calc.calculateTrack(constA, constB, constC, result);
        int expected = 1096;

        assertEquals(expected, actual);
    }


}
