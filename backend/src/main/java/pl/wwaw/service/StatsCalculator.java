package pl.wwaw.service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pl.wwaw.dto.LineStats;
import pl.wwaw.model.DelayReport;
import pl.wwaw.model.Line;

public final class StatsCalculator {

    public static final int MIN_REPORTS = 3;

    private StatsCalculator() {
    }

    public static LineStats compute(Line line, List<DelayReport> reports, int windowDays, ZoneId zone) {
        int count = reports.size();
        if (count == 0) {
            return new LineStats(line.getId(), line.getNumber(), line.getType(), 0, 0, 0, 0, null, null,
                    "N/A", windowDays);
        }

        int[] sorted = reports.stream().mapToInt(DelayReport::getDelayMinutes).sorted().toArray();
        double average = round1(Arrays.stream(sorted).average().orElse(0));
        int p90 = sorted[(int) Math.ceil(0.9 * count) - 1];
        int max = sorted[count - 1];

        Integer worstHour = null;
        Double worstHourAverage = null;
        String rating = "N/A";
        if (count >= MIN_REPORTS) {
            Map<Integer, Double> averageByHour = reports.stream().collect(Collectors.groupingBy(
                    r -> r.getReportedAt().atZone(zone).getHour(),
                    TreeMap::new,
                    Collectors.averagingInt(DelayReport::getDelayMinutes)));
            Map.Entry<Integer, Double> worst = averageByHour.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow();
            worstHour = worst.getKey();
            worstHourAverage = round1(worst.getValue());
            rating = rate(average);
        }

        return new LineStats(line.getId(), line.getNumber(), line.getType(), count, average, p90, max,
                worstHour, worstHourAverage, rating, windowDays);
    }

    static String rate(double averageDelay) {
        if (averageDelay < 3) {
            return "A";
        }
        if (averageDelay < 6) {
            return "B";
        }
        if (averageDelay < 10) {
            return "C";
        }
        return "D";
    }

    private static double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
