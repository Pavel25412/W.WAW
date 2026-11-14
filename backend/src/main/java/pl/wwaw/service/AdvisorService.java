package pl.wwaw.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.dto.AdvisorResponse;
import pl.wwaw.dto.LineStats;
import pl.wwaw.dto.TicketTypeResponse;
import pl.wwaw.exception.BadRequestException;
import pl.wwaw.exception.NotFoundException;
import pl.wwaw.model.FareType;
import pl.wwaw.model.TicketType;
import pl.wwaw.repository.TicketTypeRepository;

@Service
public class AdvisorService {

    private static final int MAX_PLANNED_MINUTES = 240;

    private final StatsService statsService;
    private final TicketTypeRepository ticketTypes;

    public AdvisorService(StatsService statsService, TicketTypeRepository ticketTypes) {
        this.statsService = statsService;
        this.ticketTypes = ticketTypes;
    }

    @Transactional(readOnly = true)
    public AdvisorResponse advise(Long lineId, int plannedMinutes, FareType fare) {
        if (plannedMinutes < 1 || plannedMinutes > MAX_PLANNED_MINUTES) {
            throw new BadRequestException("Czas przejazdu musi mieścić się w przedziale 1-" + MAX_PLANNED_MINUTES + " minut.");
        }

        LineStats stats = statsService.forLine(lineId);
        boolean hasData = stats.reportCount() >= StatsCalculator.MIN_REPORTS;
        int buffer = hasData ? stats.p90DelayMinutes() : 0;
        int required = plannedMinutes + buffer;

        List<TicketType> candidates = ticketTypes.findAllByOrderByValidityMinutesAscFareAsc().stream()
                .filter(t -> t.getFare() == fare)
                .toList();
        if (candidates.isEmpty()) {
            throw new NotFoundException("Brak biletów w wybranej taryfie.");
        }

        TicketType recommended = cheapestCovering(candidates, required);
        TicketType baseline = cheapestCovering(candidates, plannedMinutes);
        boolean upgraded = !recommended.getId().equals(baseline.getId());

        return new AdvisorResponse(lineId, stats.lineNumber(), plannedMinutes, buffer, required, hasData, upgraded,
                TicketTypeResponse.from(recommended), reason(stats, hasData, plannedMinutes, buffer, required,
                        recommended, baseline, upgraded));
    }

    private static TicketType cheapestCovering(List<TicketType> candidates, int minutes) {
        return candidates.stream()
                .filter(t -> t.getValidityMinutes() >= minutes)
                .min(Comparator.comparing(TicketType::getPrice).thenComparingInt(TicketType::getValidityMinutes))
                .orElseGet(() -> candidates.stream()
                        .max(Comparator.comparingInt(TicketType::getValidityMinutes))
                        .orElseThrow());
    }

    private static String reason(LineStats stats, boolean hasData, int planned, int buffer, int required,
                                 TicketType recommended, TicketType baseline, boolean upgraded) {
        if (!hasData) {
            return "Dla linii " + stats.lineNumber() + " jest za mało zgłoszeń opóźnień (" + stats.reportCount()
                    + " z minimum " + StatsCalculator.MIN_REPORTS + "), więc dobieramy bilet tylko do czasu przejazdu: "
                    + planned + " min. Polecamy: " + recommended.getName() + ".";
        }
        String text = "Linia " + stats.lineNumber() + " ma w ostatnich " + stats.windowDays()
                + " dniach opóźnienia do " + buffer + " min w 90% zgłoszeń. Przejazd " + planned + " min + zapas "
                + buffer + " min = " + required + " min. Polecamy: " + recommended.getName() + ".";
        if (upgraded) {
            text += " Bez uwzględnienia opóźnień wystarczyłby: " + baseline.getName() + ".";
        }
        return text;
    }
}
