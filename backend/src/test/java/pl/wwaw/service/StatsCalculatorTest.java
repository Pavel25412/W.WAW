package pl.wwaw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.wwaw.dto.LineStats;
import pl.wwaw.model.DelayReport;
import pl.wwaw.model.Line;
import pl.wwaw.model.TransportType;

class StatsCalculatorTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");
    private static final Line LINE = new Line("175", "Lotnisko Chopina - Płochocińska", TransportType.BUS);

    private static DelayReport report(int delay, int hour) {
        Instant at = ZonedDateTime.of(2026, 11, 2, hour, 15, 0, 0, WARSAW).toInstant();
        return new DelayReport(LINE, delay, null, null, at);
    }

    @Test
    void noReportsGivesEmptyStatsWithoutRating() {
        LineStats stats = StatsCalculator.compute(LINE, List.of(), 30, WARSAW);

        assertThat(stats.reportCount()).isZero();
        assertThat(stats.rating()).isEqualTo("N/A");
        assertThat(stats.worstHour()).isNull();
    }

    @Test
    void tooFewReportsAreNotRated() {
        LineStats stats = StatsCalculator.compute(LINE, List.of(report(5, 8), report(7, 8)), 30, WARSAW);

        assertThat(stats.reportCount()).isEqualTo(2);
        assertThat(stats.averageDelayMinutes()).isEqualTo(6.0);
        assertThat(stats.rating()).isEqualTo("N/A");
        assertThat(stats.worstHour()).isNull();
    }

    @Test
    void computesAveragePercentileMaxAndRating() {
        List<DelayReport> reports = List.of(report(2, 8), report(4, 8), report(6, 8), report(8, 8), report(10, 8));

        LineStats stats = StatsCalculator.compute(LINE, reports, 30, WARSAW);

        assertThat(stats.averageDelayMinutes()).isEqualTo(6.0);
        assertThat(stats.p90DelayMinutes()).isEqualTo(10);
        assertThat(stats.maxDelayMinutes()).isEqualTo(10);
        assertThat(stats.rating()).isEqualTo("C");
        assertThat(stats.windowDays()).isEqualTo(30);
    }

    @Test
    void findsTheHourWithTheHighestAverageDelay() {
        List<DelayReport> reports = List.of(report(2, 6), report(3, 6), report(12, 17), report(14, 17));

        LineStats stats = StatsCalculator.compute(LINE, reports, 30, WARSAW);

        assertThat(stats.worstHour()).isEqualTo(17);
        assertThat(stats.worstHourAverageDelay()).isEqualTo(13.0);
    }

    @Test
    void ratingThresholds() {
        assertThat(StatsCalculator.rate(2.9)).isEqualTo("A");
        assertThat(StatsCalculator.rate(3)).isEqualTo("B");
        assertThat(StatsCalculator.rate(6)).isEqualTo("C");
        assertThat(StatsCalculator.rate(10)).isEqualTo("D");
    }
}
