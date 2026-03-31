package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScoringService {

    private static class Formula {
        double A;
        double B;
        double C;
        boolean track;
        double min;
        double max;

        Formula(double A, double B, double C, boolean track, double min, double max) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.track = track;
            this.min = min;
            this.max = max;
        }
    }

    private final Map<String, Formula> formulas = new HashMap<>();

    public ScoringService() {
        formulas.put("100m", new Formula(25.4347, 18, 1.81, true, 5, 20));
        formulas.put("longJump", new Formula(0.14354, 220, 1.4, false, 0, 1000));
        formulas.put("shotPut", new Formula(51.39, 1.5, 1.05, false, 0, 30));
        formulas.put("highJump", new Formula(0.8465, 75, 1.42, false, 0, 300));
        formulas.put("400m", new Formula(1.53775, 82, 1.81, true, 20, 100));
        formulas.put("110mHurdles", new Formula(5.74352, 28.5, 1.92, true, 10, 30));
        formulas.put("discusThrow", new Formula(12.91, 4, 1.1, false, 0, 85));
        formulas.put("poleVault", new Formula(0.2797, 100, 1.35, false, 0, 1000));
        formulas.put("javelinThrow", new Formula(10.14, 7, 1.08, false, 0, 110));
        formulas.put("1500m", new Formula(0.03768, 480, 1.85, true, 150, 400));
        formulas.put("100mHurdles", new Formula(9.23076, 26.7, 1.835, true, 10, 30));
        formulas.put("200m", new Formula(4.99087, 42.5, 1.81, true, 20, 100));
        formulas.put("800m", new Formula(0.11193, 254, 1.88, true, 70, 250));
    }

    public int score(String event, double raw) {
        Formula f = formulas.get(event);

        if (f == null) {
            throw new IllegalArgumentException("Unknown event");
        }

        if (raw < f.min || raw > f.max) {
            throw new IllegalArgumentException("Result out of range");
        }

        if (f.track) {
            return (int) (f.A * Math.pow((f.B - raw), f.C));
        } else {
            return (int) (f.A * Math.pow((raw - f.B), f.C));
        }
    }
}