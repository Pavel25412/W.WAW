package pl.wwaw.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.wwaw.dto.LineStats;
import pl.wwaw.service.StatsService;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/lines/{id}/stats")
    public LineStats forLine(@PathVariable Long id) {
        return statsService.forLine(id);
    }

    @GetMapping("/stats/lines")
    public List<LineStats> forAllLines() {
        return statsService.forAllLines();
    }
}
