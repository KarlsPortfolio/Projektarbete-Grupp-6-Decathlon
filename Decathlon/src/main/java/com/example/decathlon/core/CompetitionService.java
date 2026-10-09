package com.example.decathlon.core;

import com.example.decathlon.core.ScoringService.Discipline;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CompetitionService {
    private final ScoringService scoring;
    private final Map<Discipline, List<Competitor>> competitors = new EnumMap<>(Discipline.class);

    public CompetitionService(ScoringService scoring) {
        this.scoring = scoring;
        for (Discipline d : Discipline.values()) {
            competitors.put(d, new ArrayList<>());
        }
    }

    public static class Competitor {
        private String baseName;
        private String name;
        public final Map<String, Integer> points = new ConcurrentHashMap<>();

        public Competitor(String name) {
            this.baseName = name;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int total() {
            return points.values().stream().mapToInt(i -> i).sum();
        }
    }

    public static class CompetitorNotFoundException extends RuntimeException {
        public CompetitorNotFoundException(String name) {
            super("Competitor \"" + name + "\" does not exist");
        }
    }

    public static class InvalidNameException extends RuntimeException {
        public InvalidNameException(String message) {
            super(message);
        }
    }

    public synchronized void addCompetitor(Discipline discipline, String name) {
        String base = name == null ? "" : name.trim();
        if (base.isEmpty()) {
            throw new InvalidNameException("Name must not be empty");
        }
        List<Competitor> list = competitors.get(discipline);
        List<Competitor> sameName = new ArrayList<>();
        for (Competitor c : list) {
            if (c.baseName.equals(base) || c.name.equals(base)) {
                sameName.add(c);
            }
        }
        if (sameName.isEmpty()) {
            list.add(new Competitor(base));
            return;
        }
        for (Competitor c : sameName) {
            if (c.name.equals(base)) {
                c.baseName = base;
                c.name = numbered(list, base, 1);
            }
        }
        Competitor added = new Competitor(base);
        added.name = numbered(list, base, sameName.size() + 1);
        list.add(added);
    }

    private String numbered(List<Competitor> list, String base, int start) {
        int n = start;
        while (true) {
            String candidate = base + "(" + n + ")";
            boolean taken = false;
            for (Competitor c : list) {
                if (c.name.equals(candidate)) {
                    taken = true;
                    break;
                }
            }
            if (!taken) {
                return candidate;
            }
            n++;
        }
    }

    private Competitor find(Discipline discipline, String name) {
        for (Competitor c : competitors.get(discipline)) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }

    public synchronized int score(String name, String eventId, double raw) {
        ScoringService.EventDef def = scoring.get(eventId);
        if (def == null) {
            throw new ScoringService.InvalidResultException("Unknown event");
        }
        String trimmed = name == null ? "" : name.trim();
        Competitor c = find(def.discipline(), trimmed);
        if (c == null) {
            throw new CompetitorNotFoundException(trimmed);
        }
        int pts = scoring.score(eventId, raw);
        c.points.put(eventId, pts);
        return pts;
    }

    public synchronized List<String> competitorNames(Discipline discipline) {
        return competitors.get(discipline).stream().map(Competitor::getName).collect(Collectors.toList());
    }

    public synchronized List<Map<String, Object>> standings(Discipline discipline) {
        return competitors.get(discipline).stream()
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", c.name);
                    m.put("scores", new LinkedHashMap<>(c.points));
                    m.put("total", c.total());
                    return m;
                })
                .sorted(Comparator.comparingInt(m -> -((Integer) m.get("total"))))
                .collect(Collectors.toList());
    }

    public synchronized String exportCsv(Discipline discipline) {
        List<Competitor> list = competitors.get(discipline);
        Set<String> eventIds = new LinkedHashSet<>();
        list.forEach(c -> eventIds.addAll(c.points.keySet()));
        List<String> header = new ArrayList<>();
        header.add("Name");
        header.addAll(eventIds);
        header.add("Total");

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", header)).append("\n");
        for (Competitor c : list) {
            List<String> row = new ArrayList<>();
            row.add(c.name);
            int sum = 0;
            for (String ev : eventIds) {
                Integer p = c.points.get(ev);
                row.add(p == null ? "" : String.valueOf(p));
                if (p != null) sum += p;
            }
            row.add(String.valueOf(sum));
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString();
    }
}