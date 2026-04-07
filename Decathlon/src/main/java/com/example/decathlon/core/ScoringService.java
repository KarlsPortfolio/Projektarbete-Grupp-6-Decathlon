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
        formulas.put("Decathlon:100m", new Formula(25.4347, 18, 1.81, true, 5, 20));
        formulas.put("Decathlon:longJump", new Formula(0.14354, 220, 1.4, false, 0, 1000));
        formulas.put("Decathlon:shotPut", new Formula(51.39, 1.5, 1.05, false, 0, 30));
        formulas.put("Decathlon:highJump", new Formula(0.8465, 75, 1.42, false, 0, 300));
        formulas.put("Decathlon:400m", new Formula(1.53775, 82, 1.81, true, 20, 100));
        formulas.put("Decathlon:110mHurdles", new Formula(5.74352, 28.5, 1.92, true, 10, 30));
        formulas.put("Decathlon:discusThrow", new Formula(12.91, 4, 1.1, false, 0, 85));
        formulas.put("Decathlon:poleVault", new Formula(0.2797, 100, 1.35, false, 0, 1000));
        formulas.put("Decathlon:javelinThrow", new Formula(10.14, 7, 1.08, false, 0, 110));
        formulas.put("Decathlon:1500m", new Formula(0.03768, 480, 1.85, true, 150, 400));

        formulas.put("Heptathlon:100mHurdles", new Formula(9.23076, 26.7, 1.835, true, 10, 30));
        formulas.put("Heptathlon:highJump", new Formula(1.84523, 75, 1.348, false, 0, 300));
        formulas.put("Heptathlon:shotPut", new Formula(56.0211, 1.5, 1.05, false, 0, 30));
        formulas.put("Heptathlon:200m", new Formula(4.99087, 42.5, 1.81, true, 20, 100));
        formulas.put("Heptathlon:longJump", new Formula(0.188807, 210, 1.41, false, 0, 1000));
        formulas.put("Heptathlon:javelinThrow", new Formula(15.9803, 3.8, 1.04, false, 0, 110));
        formulas.put("Heptathlon:800m", new Formula(0.11193, 254, 1.88, true, 70, 250));
    }

    public int score(String multiEventType, String event, double raw) {
        if (multiEventType == null || multiEventType.isBlank()) {
            throw new IllegalArgumentException("multiEventType is required");
        }

        Formula f = formulas.get(multiEventType + ":" + event);

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