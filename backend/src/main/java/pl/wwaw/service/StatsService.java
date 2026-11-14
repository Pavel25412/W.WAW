package pl.wwaw.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.config.AppConfig;
import pl.wwaw.config.DelayProperties;
import pl.wwaw.dto.LineStats;
import pl.wwaw.model.DelayReport;
import pl.wwaw.model.Line;
import pl.wwaw.repository.DelayReportRepository;

@Service
public class StatsService {

    private final DelayReportRepository reports;
    private final LineService lineService;
    private final DelayProperties properties;
    private final Clock clock;

    public StatsService(DelayReportRepository reports, LineService lineService, DelayProperties properties,
                        Clock clock) {
        this.reports = reports;
        this.lineService = lineService;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public LineStats forLine(Long lineId) {
        Line line = lineService.getEntity(lineId);
        List<DelayReport> found = reports.findByLineIdAndReportedAtAfter(lineId, cutoff());
        return StatsCalculator.compute(line, found, properties.retentionDays(), AppConfig.WARSAW);
    }

    @Transactional(readOnly = true)
    public List<LineStats> forAllLines() {
        Map<Long, List<DelayReport>> byLine = reports.findByReportedAtAfter(cutoff()).stream()
                .collect(Collectors.groupingBy(r -> r.getLine().getId()));
        return lineService.findAllEntities().stream()
                .map(line -> StatsCalculator.compute(line, byLine.getOrDefault(line.getId(), List.of()),
                        properties.retentionDays(), AppConfig.WARSAW))
                .sorted(Comparator.comparing((LineStats s) -> s.reportCount() == 0)
                        .thenComparing(LineStats::averageDelayMinutes, Comparator.reverseOrder()))
                .toList();
    }

    private Instant cutoff() {
        return Instant.now(clock).minus(properties.retention());
    }
}
