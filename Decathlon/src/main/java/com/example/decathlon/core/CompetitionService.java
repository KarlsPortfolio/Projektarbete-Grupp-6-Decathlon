package com.example.decathlon.core;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CompetitionService {
    private final ScoringService scoring;

    public CompetitionService(ScoringService scoring) {
        this.scoring = scoring;
    }

    public static class Competitor {
        public final String name;
        public final String multiEventType;
        public final Map<String, Integer> points = new ConcurrentHashMap<>();

        public Competitor(String name, String multiEventType) {
            this.name = name;
            this.multiEventType = multiEventType;
        }

        public int total() {
            return points.values().stream().mapToInt(i -> i).sum();
        }
    }

    private final Map<String, Competitor> competitors = new LinkedHashMap<>();

    private String competitorKey(String name, String multiEventType) {
        return normalizeType(multiEventType) + "|" + name;
    }

    private String normalizeType(String multiEventType) {
        if (multiEventType == null || multiEventType.isBlank()) {
            throw new IllegalArgumentException("multiEventType is required");
        }

        if ("Decathlon".equalsIgnoreCase(multiEventType)) {
            return "Decathlon";
        }

        if ("Heptathlon".equalsIgnoreCase(multiEventType)) {
            return "Heptathlon";
        }

        throw new IllegalArgumentException("Unknown multiEventType");
    }

    public synchronized void addCompetitor(String name, String multiEventType) {
        String normalizedType = normalizeType(multiEventType);
        String key = competitorKey(name, normalizedType);

        if (!competitors.containsKey(key)) {
            competitors.put(key, new Competitor(name, normalizedType));
        }
    }

    public synchronized boolean deleteCompetitor(String name, String multiEventType) {
        return competitors.remove(competitorKey(name, multiEventType)) != null;
    }

    public synchronized int score(String name, String multiEventType, String eventId, double raw) {
        String normalizedType = normalizeType(multiEventType);
        String key = competitorKey(name, normalizedType);
        Competitor c = competitors.computeIfAbsent(key, k -> new Competitor(name, normalizedType));
//        double raw;

//        if(rawValue.contains(",")){
//            rawValue = rawValue.replace(',', '.');
//            raw = Double.parseDouble(rawValue);
//
//
//        }

        int pts = scoring.score(normalizedType, eventId, raw);
        c.points.put(eventId, pts);
        return pts;
    }

    public synchronized void setPoints(String name, String multiEventType, String eventId, int points) {
        String normalizedType = normalizeType(multiEventType);
        String key = competitorKey(name, normalizedType);
        Competitor c = competitors.computeIfAbsent(key, k -> new Competitor(name, normalizedType));
        c.points.put(eventId, points);
    }

    public synchronized void clear() {
        competitors.clear();
    }

    public synchronized Map<String, Object> standings() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("decathlon", buildStandingsFor("Decathlon"));
        result.put("heptathlon", buildStandingsFor("Heptathlon"));
        return result;
    }

    private List<Map<String, Object>> buildStandingsFor(String multiEventType) {
        return competitors.values().stream()
                .filter(c -> c.multiEventType.equals(multiEventType))
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

    public synchronized String exportCsv() {
        StringBuilder sb = new StringBuilder();
        exportGroup(sb, "Decathlon");
        exportGroup(sb, "Heptathlon");
        return sb.toString();
    }

    private void exportGroup(StringBuilder sb, String multiEventType) {
        List<Competitor> group = competitors.values().stream()
                .filter(c -> c.multiEventType.equals(multiEventType))
                .collect(Collectors.toList());

        if (group.isEmpty()) {
            return;
        }

        Set<String> eventIds = new LinkedHashSet<>();
        group.forEach(c -> eventIds.addAll(c.points.keySet()));

        List<String> header = new ArrayList<>();
        header.add("MultiEventType");
        header.add("Name");
        header.addAll(eventIds);
        header.add("Total");

        sb.append(String.join(",", header)).append("\n");

        for (Competitor c : group) {
            List<String> row = new ArrayList<>();
            row.add(c.multiEventType);
            row.add(c.name);
            int sum = 0;
            for (String ev : eventIds) {
                Integer p = c.points.get(ev);
                row.add(p == null ? "" : String.valueOf(p));
                if (p != null) {
                    sum += p;
                }
            }
            row.add(String.valueOf(sum));
            sb.append(String.join(",", row)).append("\n");
        }
    }

    public synchronized void importCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new IllegalArgumentException("File is empty");
        }

        String[] lines = csv.replace("\r", "").split("\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }

        competitors.clear();

        String currentType = null;
        List<String> events = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            String[] cells = line.split(",", -1);

            if (cells.length >= 3 && "MultiEventType".equals(cells[0]) && "Name".equals(cells[1])) {
                currentType = null;
                events.clear();
                for (int i = 2; i < cells.length - 1; i++) {
                    events.add(cells[i]);
                }
                continue;
            }

            if (cells.length < 2) {
                throw new IllegalArgumentException("Invalid CSV format");
            }

            String multiEventType = cells[0].trim();
            String name = cells[1].trim();

            if (multiEventType.isEmpty() || name.isEmpty()) {
                continue;
            }

            currentType = normalizeType(multiEventType);
            String key = competitorKey(name, currentType);
            Competitor competitor = new Competitor(name, currentType);

            for (int i = 0; i < events.size(); i++) {
                int cellIndex = i + 2;
                if (cellIndex >= cells.length) {
                    continue;
                }

                String value = cells[cellIndex].trim();
                if (value.isEmpty()) {
                    continue;
                }

                try {
                    competitor.points.put(events.get(i), Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid CSV format");
                }
            }

            competitors.put(key, competitor);
        }
    }

    public int count(String multiEventType) {
        return buildStandingsFor(normalizeType(multiEventType)).size();
    }
}