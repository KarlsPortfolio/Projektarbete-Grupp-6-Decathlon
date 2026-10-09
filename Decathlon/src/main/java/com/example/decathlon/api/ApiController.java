package com.example.decathlon.api;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.core.ScoringService;
import com.example.decathlon.dto.ScoreReq;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final int MAX_COMPETITORS = 40;

    private final CompetitionService comp;
    private final ScoringService scoring;

    public ApiController(CompetitionService comp, ScoringService scoring) {
        this.comp = comp;
        this.scoring = scoring;
    }

    private static ScoringService.Discipline parseDiscipline(String discipline) {
        return "heptathlon".equalsIgnoreCase(discipline)
                ? ScoringService.Discipline.HEPTATHLON
                : ScoringService.Discipline.DECATHLON;
    }

    @PostMapping("/competitors")
    public ResponseEntity<?> add(@RequestBody Map<String,String> body) {
        String name = Optional.ofNullable(body.get("name")).orElse("");
        ScoringService.Discipline discipline = parseDiscipline(body.get("discipline"));

        if (comp.competitorNames(discipline).size() >= MAX_COMPETITORS) {
            return ResponseEntity.status(429).body("Too many competitors");
        }

        try {
            comp.addCompetitor(discipline, name);
        } catch (CompetitionService.InvalidNameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/competitors")
    public List<String> competitors(@RequestParam(value = "discipline", defaultValue = "decathlon") String discipline) {
        return comp.competitorNames(parseDiscipline(discipline));
    }

    @PostMapping("/score")
    public ResponseEntity<?> score(@RequestBody ScoreReq r) {
        if (r.raw() == null) {
            return ResponseEntity.badRequest().body("Please enter a valid number for the result");
        }
        try {
            int pts = comp.score(r.name(), r.event(), r.raw());
            return ResponseEntity.ok(Map.of("points", pts));
        } catch (CompetitionService.CompetitorNotFoundException | ScoringService.InvalidResultException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/standings")
    public List<Map<String,Object>> standings(@RequestParam(value = "discipline", defaultValue = "decathlon") String discipline) {
        return comp.standings(parseDiscipline(discipline));
    }

    @GetMapping("/events")
    public List<Map<String,Object>> events(@RequestParam(value = "discipline", defaultValue = "decathlon") String discipline) {
        return scoring.eventDefs(parseDiscipline(discipline)).stream()
                .map(e -> {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", e.id());
                    m.put("label", e.label());
                    m.put("unit", e.unit());
                    return (Map<String,Object>) m;
                })
                .toList();
    }

    @GetMapping(value="/export.csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public String export(@RequestParam(value = "discipline", defaultValue = "decathlon") String discipline) {
        return comp.exportCsv(parseDiscipline(discipline));
    }
}