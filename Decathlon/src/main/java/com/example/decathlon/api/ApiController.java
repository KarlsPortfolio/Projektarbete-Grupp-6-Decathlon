package com.example.decathlon.api;

import com.example.decathlon.core.CompetitionService;
import com.example.decathlon.dto.ScoreReq;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CompetitionService comp;

    public ApiController(CompetitionService comp) {
        this.comp = comp;
    }

    @PostMapping("/competitors")
    public ResponseEntity<?> add(@RequestBody Map<String, String> body) {
        String name = Optional.ofNullable(body.get("name")).orElse("").trim();

        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (getCount() >= 40) {
            return ResponseEntity.status(429).body("Too many competitors");
        }

        comp.addCompetitor(name);
        return ResponseEntity.status(201).build();
    }

    private int getCount() {
        return comp.standings().size();
    }

    @PostMapping("/score")
    public ResponseEntity<?> score(@RequestBody ScoreReq r) {
        try {
            int pts = comp.score(r.name(), r.event(), r.raw());
            return ResponseEntity.ok(Map.of("points", pts));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/standings")
    public List<Map<String, Object>> standings() {
        return comp.standings();
    }

    @GetMapping(value = "/export.csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public String export() {
        return comp.exportCsv();
    }

    @PostMapping(value = "/import.csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is required");
            }
            comp.importCsv(new String(file.getBytes()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import failed");
        }
    }
}