package pl.wwaw.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private TicketOrder order;

    @ManyToOne(optional = false)
    private TicketType ticketType;

    @Column(name = "owner_id", nullable = false, length = 64)
    private String ownerId;

    @Column(nullable = false, length = 16)
    private String code;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    public Ticket(TicketOrder order, Instant createdAt) {
        this.order = order;
        this.ticketType = order.getTicketType();
        this.ownerId = order.getOwnerId();
        this.code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = createdAt;
    }

    public void validate(Instant now) {
        this.validatedAt = now;
        this.expiresAt = now.plus(ticketType.getValidityMinutes(), ChronoUnit.MINUTES);
    }

    public TicketStatus statusAt(Instant now) {
        if (validatedAt == null) {
            return TicketStatus.NOT_VALIDATED;
        }
        return now.isBefore(expiresAt) ? TicketStatus.ACTIVE : TicketStatus.EXPIRED;
    }
}
