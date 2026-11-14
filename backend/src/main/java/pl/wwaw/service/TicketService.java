package pl.wwaw.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.dto.TicketResponse;
import pl.wwaw.exception.ConflictException;
import pl.wwaw.exception.NotFoundException;
import pl.wwaw.model.Ticket;
import pl.wwaw.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository tickets;
    private final Clock clock;

    public TicketService(TicketRepository tickets, Clock clock) {
        this.tickets = tickets;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findByOwner(String ownerId) {
        Instant now = Instant.now(clock);
        return tickets.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(t -> TicketResponse.from(t, now))
                .toList();
    }

    @Transactional
    public TicketResponse validate(Long ticketId, String ownerId) {
        Ticket ticket = tickets.findById(ticketId)
                .filter(t -> t.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new NotFoundException("Nie znaleziono biletu o id " + ticketId + "."));
        if (ticket.getValidatedAt() != null) {
            throw new ConflictException("Ten bilet został już skasowany.");
        }
        Instant now = Instant.now(clock);
        ticket.validate(now);
        return TicketResponse.from(ticket, now);
    }
}
