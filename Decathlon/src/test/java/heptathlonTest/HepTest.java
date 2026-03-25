package heptathlonTest;

import com.example.decathlon.common.CalcTrackAndField;
import com.example.decathlon.deca.DecaHighJump;
import com.example.decathlon.heptathlon.HeptJavelinThrow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HepTest {

    private double constA, constB, constC, result;

    @Test
    public void testHepValidInput200M() {
        CalcTrackAndField calc = new CalcTrackAndField();

        constA = 4.99087;
        constB = 42.5;
        constC = 1.81;
        result = 20;

        int actual = calc.calculateTrack(constA, constB, constC, result);
        int expected = 1398;
        //int expected = (int) (constA * Math.pow((constB - result), constC));

        assertEquals(expected, actual);
    }
    @Test
    public void testHepValidInputJavelinThrow(){
        HeptJavelinThrow javelinThrow = new HeptJavelinThrow();
        int actual = javelinThrow.calculateResult(100);
        int expected = 1845;
        assertEquals(expected,actual);
    }

}
