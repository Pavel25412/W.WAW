package pl.wwaw.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_types")
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "validity_minutes", nullable = false)
    private int validityMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FareType fare;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    protected TicketType() {
    }

    public TicketType(String name, String description, int validityMinutes, FareType fare, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.validityMinutes = validityMinutes;
        this.fare = fare;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getValidityMinutes() { return validityMinutes; }
    public FareType getFare() { return fare; }
    public BigDecimal getPrice() { return price; }
}
