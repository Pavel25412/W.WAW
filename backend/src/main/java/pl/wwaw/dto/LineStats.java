package pl.wwaw.dto;

import pl.wwaw.model.TransportType;


public record LineStats(Long lineId, String lineNumber, TransportType lineType, int reportCount,
                        double averageDelayMinutes, int p90DelayMinutes, int maxDelayMinutes,
                        Integer worstHour, Double worstHourAverageDelay, String rating, int windowDays) {
}
