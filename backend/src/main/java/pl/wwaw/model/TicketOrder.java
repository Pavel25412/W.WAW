package pl.wwaw.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "ticket_orders")
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "owner_id", nullable = false, length = 64)
    private String ownerId;

    @ManyToOne(optional = false)
    private TicketType ticketType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderStatus status;

    @Column(name = "payment_reference", length = 64)
    private String paymentReference;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected TicketOrder() {
    }

    public TicketOrder(String ownerId, TicketType ticketType, int quantity, Instant createdAt) {
        this.ownerId = ownerId;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.totalAmount = ticketType.getPrice().multiply(BigDecimal.valueOf(quantity));
        this.status = OrderStatus.PENDING;
        this.createdAt = createdAt;
    }

    public void markPaid(String paymentReference, Instant paidAt) {
        this.status = OrderStatus.PAID;
        this.paymentReference = paymentReference;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public TicketType getTicketType() { return ticketType; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public String getPaymentReference() { return paymentReference; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPaidAt() { return paidAt; }
}
