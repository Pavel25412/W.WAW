package pl.wwaw.dto;

import java.time.Instant;

import pl.wwaw.model.Ticket;
import pl.wwaw.model.TicketStatus;

public record TicketResponse(Long id, String code, TicketTypeResponse ticketType, TicketStatus status,
                             Instant createdAt, Instant validatedAt, Instant expiresAt) {

    public static TicketResponse from(Ticket t, Instant now) {
        return new TicketResponse(t.getId(), t.getCode(), TicketTypeResponse.from(t.getTicketType()),
                t.statusAt(now), t.getCreatedAt(), t.getValidatedAt(), t.getExpiresAt());
    }
}
