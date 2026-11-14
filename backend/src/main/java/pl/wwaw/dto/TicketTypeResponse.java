package pl.wwaw.dto;

import java.math.BigDecimal;

import pl.wwaw.model.FareType;
import pl.wwaw.model.TicketType;

public record TicketTypeResponse(Long id, String name, String description, int validityMinutes, FareType fare,
                                 BigDecimal price) {

    public static TicketTypeResponse from(TicketType t) {
        return new TicketTypeResponse(t.getId(), t.getName(), t.getDescription(), t.getValidityMinutes(),
                t.getFare(), t.getPrice());
    }
}
