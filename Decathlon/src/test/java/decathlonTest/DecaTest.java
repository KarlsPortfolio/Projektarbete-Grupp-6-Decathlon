package decathlonTest;

import com.example.decathlon.common.CalcTrackAndField;
import com.example.decathlon.deca.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecaTest {

    private double constA, constB, constC, result;


    @Test
    public void testDecaValidInput110M(){
        Deca110MHurdles calc = new Deca110MHurdles();

        result = 15;

        int actual =  calc.calculateResult(result);
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

    @Test
    public void testDecaValidInputHighJump(){
        DecaHighJump highJump = new DecaHighJump();
        int actual = highJump.calculateResult(90);
        int expected = 39;
        assertEquals(expected,actual);
    }

    @Test
    public void testDecaUpperLimitHighJump(){
        DecaHighJump highJump = new DecaHighJump();
        int actual = highJump.calculateResult(300);
        int expected = 1852;
        assertEquals(expected,actual);
    }


    @Test
    public void testDecaLowerLimitHighJump(){
        DecaHighJump highJump = new DecaHighJump();
        int actual = highJump.calculateResult(75);
        int expected = 0;
        assertEquals(expected,actual);
    }


    @Test
    public void testDecaValidInput400M() {
        Deca100M calc = new Deca100M();

        result = 42;

        int actual = calc.calculateResult(result);
        int expected = 1220;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaUpperLimit400M() {
        Deca400M calc = new Deca400M();

        result = 82;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaLowerLimit400M() {
        Deca400M calc = new Deca400M();

        result = 20;

        int actual = calc.calculateResult(result);
        int expected = 2698;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidInputPoleVault() {
        DecaPoleVault calc = new DecaPoleVault();

        result = 555.55;

        int actual = calc.calculateResult(result);
        int expected = 1085;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaUpperLimitPoleVault() {
        DecaPoleVault calc = new DecaPoleVault();

        result = 1000;

        int actual = calc.calculateResult(result);
        int expected = 2722;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaLowerLimitPoleVault() {
        DecaPoleVault calc = new DecaPoleVault();

        result = 100;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidInputShotPut() {
        DecaShotPut calc = new DecaShotPut();

        result = 21.12;

        int actual = calc.calculateResult(result);
        int expected = 1170;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaUpperLimitShotPut() {
        DecaShotPut calc = new DecaShotPut();

        result = 30;

        int actual = calc.calculateResult(result);
        int expected = 1731;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaLowerLimitShotPut() {
        DecaShotPut calc = new DecaShotPut();

        result = 1.5;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidInput100M() {
        Deca100M calc = new Deca100M();

        constA = 25.4347;
        constB = 18;
        constC = 1.81;
        result = 10;

        int actual = calc.calculateResult(result);
        int expected = 1096;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidLowerLimit100M() {
        Deca100M calc = new Deca100M();

        result = 5;

        int actual = calc.calculateResult(result);
        int expected = 2640;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidUpperLimit100M() {
        Deca100M calc = new Deca100M();

        result = 18;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidJavelinThrow() {
        CalcTrackAndField calc = new CalcTrackAndField();

        constA = 10.14;
        constB = 7;
        constC = 1.08;
        result = 15;

        int actual = calc.calculateField(constA, constB, constC, result);
        int expected = 95;

        assertEquals(expected, actual);
    }

    @Test
    public void testDecaValidUpperLimitJavelinThrow() {
        DecaJavelinThrow calc = new DecaJavelinThrow();

        result = 110;

        int actual = calc.calculateResult(result);
        int expected = 1513;

        assertEquals(expected, actual);
    }
    @Test
    public void testDecaValidLowerLimitJavelinThrow() {
        DecaJavelinThrow calc = new DecaJavelinThrow();

        result = 7;

        int actual = calc.calculateResult(result);
        int expected = 0;

        assertEquals(expected, actual);
    }

}
