package pl.wwaw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lines")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number", nullable = false, length = 10)
    private String number;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, length = 10)
    private TransportType type;

    protected Line() {
    }

    public Line(String number, String name, TransportType type) {
        this.number = number;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }
    public String getNumber() { return number; }
    public String getName() { return name; }
    public TransportType getType() { return type; }

    public void setNumber(String number) { this.number = number; }
    public void setName(String name) { this.name = name; }
    public void setType(TransportType type) { this.type = type; }
}
