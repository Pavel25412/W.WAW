package pl.wwaw.dto;

import java.time.Duration;
import java.time.Instant;

import pl.wwaw.model.DelayReport;
import pl.wwaw.model.TransportType;

public record DelayResponse(Long id, Long lineId, String lineNumber, TransportType lineType, int delayMinutes,
                            String stopName, String comment, Instant reportedAt, Instant expiresAt) {

    public static DelayResponse from(DelayReport r, Duration retention) {
        return new DelayResponse(r.getId(), r.getLine().getId(), r.getLine().getNumber(), r.getLine().getType(),
                r.getDelayMinutes(), r.getStopName(), r.getComment(), r.getReportedAt(),
                r.getReportedAt().plus(retention));
    }
}
