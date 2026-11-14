package pl.wwaw.config;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pl.wwaw.model.DelayReport;
import pl.wwaw.model.FareType;
import pl.wwaw.model.Line;
import pl.wwaw.model.TicketType;
import pl.wwaw.model.TransportType;
import pl.wwaw.repository.DelayReportRepository;
import pl.wwaw.repository.LineRepository;
import pl.wwaw.repository.TicketTypeRepository;


@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String[] STOPS = {"Dworzec Centralny", "Rondo ONZ", "Plac Zawiszy", "Dworzec Wschodni",
            "Metro Politechnika", "Rondo Daszyńskiego", "Plac Narutowicza", "Metro Centrum"};
    private static final String[] COMMENTS = {"Korki na trasie", "Awaria pojazdu", "Objazd z powodu remontu",
            "Zdarzenie na torowisku", "Tłok przy wsiadaniu", null, null, null};
    private static final int[] RUSH_HOURS = {7, 8, 9, 15, 16, 17, 18};

    private final LineRepository lines;
    private final TicketTypeRepository ticketTypes;
    private final DelayReportRepository delays;
    private final Clock clock;

    public DataSeeder(LineRepository lines, TicketTypeRepository ticketTypes, DelayReportRepository delays,
                      Clock clock) {
        this.lines = lines;
        this.ticketTypes = ticketTypes;
        this.delays = delays;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        seedTicketTypes();
        List<Line> savedLines = seedLines();
        seedDelays(savedLines);
        log.info("Seeded {} lines, {} ticket types and demo delay reports", savedLines.size(), ticketTypes.count());
    }

    private void seedTicketTypes() {
        ticketTypes.saveAll(List.of(
                new TicketType("Bilet 20-minutowy", "Ważny 20 minut od skasowania, strefy 1+2.", 20,
                        FareType.NORMAL, new BigDecimal("3.40")),
                new TicketType("Bilet 20-minutowy", "Ważny 20 minut od skasowania, strefy 1+2.", 20,
                        FareType.REDUCED, new BigDecimal("1.70")),
                new TicketType("Bilet 75-minutowy", "Ważny 75 minut od skasowania, strefa 1.", 75,
                        FareType.NORMAL, new BigDecimal("4.40")),
                new TicketType("Bilet 75-minutowy", "Ważny 75 minut od skasowania, strefa 1.", 75,
                        FareType.REDUCED, new BigDecimal("2.20")),
                new TicketType("Bilet 90-minutowy", "Ważny 90 minut od skasowania, strefy 1+2.", 90,
                        FareType.NORMAL, new BigDecimal("7.00")),
                new TicketType("Bilet 90-minutowy", "Ważny 90 minut od skasowania, strefy 1+2.", 90,
                        FareType.REDUCED, new BigDecimal("3.50")),
                new TicketType("Bilet dobowy (24 h)", "Nielimitowane przejazdy przez 24 godziny, strefa 1.", 1440,
                        FareType.NORMAL, new BigDecimal("15.00")),
                new TicketType("Bilet dobowy (24 h)", "Nielimitowane przejazdy przez 24 godziny, strefa 1.", 1440,
                        FareType.REDUCED, new BigDecimal("7.50"))));
    }

    private List<Line> seedLines() {
        return lines.saveAll(List.of(
                new Line("M1", "Kabaty - Młociny", TransportType.METRO),
                new Line("M2", "Rondo Daszyńskiego - Trocka", TransportType.METRO),
                new Line("9", "Gocławek - Os. Górczewska", TransportType.TRAM),
                new Line("175", "Lotnisko Chopina - Płochocińska", TransportType.BUS),
                new Line("S1", "Pruszków - Otwock", TransportType.SKM),
                new Line("S3", "Wołomin - Warszawa Zachodnia", TransportType.SKM),
                new Line("WKD", "Warszawa Śródmieście WKD - Grodzisk Maz. Radońska", TransportType.WKD)));
    }

    private void seedDelays(List<Line> savedLines) {
        Random random = new Random(42);
        ZonedDateTime now = ZonedDateTime.now(clock.withZone(AppConfig.WARSAW));
        List<DelayReport> reports = new ArrayList<>();

        for (Line line : savedLines) {
            int count = line.getType() == TransportType.METRO ? 4 : 18 + random.nextInt(12);
            for (int i = 0; i < count; i++) {
                int hour = random.nextInt(10) < 6 ? RUSH_HOURS[random.nextInt(RUSH_HOURS.length)]
                        : 5 + random.nextInt(19);
                boolean rush = hour >= 7 && hour <= 9 || hour >= 15 && hour <= 18;
                long minutes = Math.round(meanDelay(line.getType()) * (0.4 + random.nextDouble() * 1.6)
                        * (rush ? 1.4 : 0.8));
                int delay = (int) Math.max(1, Math.min(60, minutes));
                var reportedAt = now.minusDays(1 + random.nextInt(28))
                        .withHour(hour).withMinute(random.nextInt(60)).toInstant();
                reports.add(new DelayReport(line, delay, STOPS[random.nextInt(STOPS.length)],
                        COMMENTS[random.nextInt(COMMENTS.length)], reportedAt));
            }
        }
        delays.saveAll(reports);
    }

    private static double meanDelay(TransportType type) {
        return switch (type) {
            case METRO -> 1.5;
            case TRAM, WKD -> 4;
            case SKM -> 5;
            case BUS -> 6;
            case KM -> 8;
        };
    }
}
