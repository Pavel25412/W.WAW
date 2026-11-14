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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public TicketType(String name, String description, int validityMinutes, FareType fare, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.validityMinutes = validityMinutes;
        this.fare = fare;
        this.price = price;
    }
}
