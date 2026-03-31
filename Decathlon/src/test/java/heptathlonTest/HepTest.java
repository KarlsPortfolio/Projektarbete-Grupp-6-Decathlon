package heptathlonTest;

import com.example.decathlon.common.CalcTrackAndField;
import com.example.decathlon.heptathlon.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HepTest {

    private double constA, constB, constC, result;

    @Test
    public void testHepValidInput200M() {
        Hep200M calc = new Hep200M();

        result = 30;

        int actual = calc.calculateResult(result);
        int expected = 482;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputUpperLimit200M() {
        Hep200M calc = new Hep200M();


        result = 42.5;

        int actual = calc.calculateResult(result);
        int expected = 0;
        //int expected = (int) (constA * Math.pow((constB - result), constC));

        assertEquals(expected, actual);
    }
    @Test
    public void testHepValidInputLowerLimit200M() {
        Hep200M calc = new Hep200M();

        result = 20;

        int actual = calc.calculateResult(result);
        int expected = 1398;

        assertEquals(expected, actual);
    }
    @Test
    public void testHepValidInputJavelinThrow(){
        HeptJavelinThrow javelinThrow = new HeptJavelinThrow();
        int actual = javelinThrow.calculateResult(85);
        int expected = 1547;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepUpperLimitJavelinThrow(){
        HeptJavelinThrow javelinThrow = new HeptJavelinThrow();
        int actual = javelinThrow.calculateResult(110);
        int expected = 2045;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepLowerLimitJavelinThrow(){
        HeptJavelinThrow javelinThrow = new HeptJavelinThrow();
        int actual = javelinThrow.calculateResult(3.8);
        int expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepValidInputLongJump(){
        HeptLongJump longJump = new HeptLongJump();
        int actual = longJump.calculateResult(300);
        int expected = 107;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepUpperLimitLongJump(){
        HeptLongJump longJump = new HeptLongJump();
        int actual = longJump.calculateResult(1000);
        int expected = 1523;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepLowerLimitLongJump(){
        HeptLongJump longJump = new HeptLongJump();
        int actual = longJump.calculateResult(210);
        int expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepValidInputShotPut(){
        HeptShotPut shotPut = new HeptShotPut();
        int actual = shotPut.calculateResult(15);
        int expected = 861;
        assertEquals(expected,actual);
    }

    @Test
    public void testHepUpperLimitShotPut(){
        HeptShotPut shotPut = new HeptShotPut();
        int actual = shotPut.calculateResult(30);
        int expected = 1887;
        assertEquals(expected,actual);
    }


    @Test
    public void testHepLowerLimitShotPut(){
        HeptShotPut shotPut = new HeptShotPut();
        int actual = shotPut.calculateResult(1.5);
        int expected = 0;
        assertEquals(expected,actual);
    }



    @Test
    public void testHepValidInput100mHurdles() {
        Hep100MHurdles calc = new Hep100MHurdles();

        result = 20;

        int actual = calc.calculateResult(result);
        int expected = 302;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputLowerLimit100mHurdles() {
        Hep100MHurdles calc = new Hep100MHurdles();

        result = 10;

        int actual = calc.calculateResult(result);
        int expected = 1617;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputUpperLimit100mHurdles() {
        Hep100MHurdles calc = new Hep100MHurdles();

        result = 26.7;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInput800m() {
        CalcTrackAndField calc = new CalcTrackAndField();

        constA = 0.11193;
        constB = 254;
        constC = 1.88;
        result = 100;

        int actual = calc.calculateTrack(constA, constB, constC, result);
        int expected = 1450;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputLowerLimit800m() {
        Hep800M calc = new Hep800M();

        result = 70;

        int actual = calc.calculateResult(result);
        int expected = 2026;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputUpperLimit800m() {
        Hep800M calc = new Hep800M();

        result = 254;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputHighJump() {
        CalcTrackAndField calc = new CalcTrackAndField();

        constA = 1.84523;
        constB =  75;
        constC = 1.348;
        result = 100;

        int actual = calc.calculateField(constA, constB, constC, result);
        int expected = 141;

        assertEquals(expected, actual);
    }
    @Test
    public void testHepValidInputLowerLimitHighJump() {
        HeptHightJump calc = new HeptHightJump();

        result = 75;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testHepValidInputUpperLimitHighJump() {
        HeptHightJump calc = new HeptHightJump();

        result = 300;

        int actual = calc.calculateResult(result);
        int expected = 2733;

        assertEquals(expected, actual);
    }



}
