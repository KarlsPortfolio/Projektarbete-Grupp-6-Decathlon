package decathlonTest;

import com.example.decathlon.common.CalcTrackAndField;
import com.example.decathlon.deca.Deca110MHurdles;
import com.example.decathlon.deca.Deca1500M;
import com.example.decathlon.deca.DecaDiscusThrow;
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

    @Test
    public void testDecaValidInput110M(){
        CalcTrackAndField calc = new CalcTrackAndField();
        constA = 5.74352;
        constB = 28.5;
        constC = 1.92;
        result = 15;

        int actual =  calc.calculateTrack(constA,constB,constC,result);
        int expected = 850;
        assertEquals(expected,actual);

    }

    @Test
    public void testDecaLowerBoundaryInput110M(){
        Deca110MHurdles hurdles = new Deca110MHurdles();
        int actual = hurdles.calculateResult(10.0);
        int expected = 1556;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaUpperBoundaryInput110M(){
        Deca110MHurdles hurdles = new Deca110MHurdles();
        int actual = hurdles.calculateResult(28.5);
        int expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaValidInput1500M(){
        Deca1500M deca1500M = new Deca1500M();

        int actual = deca1500M.calculateResult(4);
        int expected = 3385;

        assertEquals(expected,actual);
    }

    @Test
    public void testDecaLowerBoundaryInput1500M(){
        Deca1500M deca1500M = new Deca1500M();
        int actual = deca1500M.calculateResult(150);
        int expected = 1719;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaUpperBoundaryInput1500M(){
        Deca1500M deca1500M = new Deca1500M();
        int actual = deca1500M.calculateResult(480);
        int expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaValidInputDiscusThrow(){
        DecaDiscusThrow discusThrow = new DecaDiscusThrow();

        int actual = discusThrow.calculateResult(36);
        int expected = 584;

        assertEquals(expected,actual);
    }

    @Test
    public void testDecaLowerBoundaryInputDiscusThrow(){
        DecaDiscusThrow discusThrow = new DecaDiscusThrow();
        int actual = discusThrow.calculateResult(4);
        int expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaUpperBoundaryInputDiscusThrow(){
        DecaDiscusThrow discusThrow = new DecaDiscusThrow();
        int actual = discusThrow.calculateResult(85);
        int expected = 1622;
        assertEquals(expected,actual);
    }
}
