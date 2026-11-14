package pl.wwaw.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.config.DelayProperties;
import pl.wwaw.dto.DelayRequest;
import pl.wwaw.dto.DelayResponse;
import pl.wwaw.model.DelayReport;
import pl.wwaw.model.Line;
import pl.wwaw.repository.DelayReportRepository;

@Service
public class DelayService {

    private static final int MAX_RESULTS = 200;

    private final DelayReportRepository reports;
    private final LineService lineService;
    private final DelayProperties properties;
    private final Clock clock;

    public DelayService(DelayReportRepository reports, LineService lineService, DelayProperties properties,
                        Clock clock) {
        this.reports = reports;
        this.lineService = lineService;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<DelayResponse> findRecent(Long lineId) {
        Instant cutoff = cutoff();
        List<DelayReport> found;
        if (lineId == null) {
            found = reports.findByReportedAtAfterOrderByReportedAtDesc(cutoff);
        } else {
            lineService.getEntity(lineId); // 404 for an unknown line
            found = reports.findByLineIdAndReportedAtAfterOrderByReportedAtDesc(lineId, cutoff);
        }
        return found.stream()
                .limit(MAX_RESULTS)
                .map(r -> DelayResponse.from(r, properties.retention()))
                .toList();
    }

    @Transactional
    public DelayResponse create(DelayRequest request) {
        Line line = lineService.getEntity(request.lineId());
        DelayReport report = new DelayReport(line, request.delayMinutes(), blankToNull(request.stopName()),
                blankToNull(request.comment()), Instant.now(clock));
        return DelayResponse.from(reports.save(report), properties.retention());
    }

    @Transactional
    public int deleteExpired() {
        return reports.deleteExpired(cutoff());
    }

    private Instant cutoff() {
        return Instant.now(clock).minus(properties.retention());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
