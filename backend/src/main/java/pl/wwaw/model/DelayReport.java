package pl.wwaw.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "delay_reports")
public class DelayReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Line line;

    @Column(name = "delay_minutes", nullable = false)
    private int delayMinutes;

    @Column(name = "stop_name", length = 80)
    private String stopName;

    @Column(name = "comment_text", length = 200)
    private String comment;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    protected DelayReport() {
    }

    public DelayReport(Line line, int delayMinutes, String stopName, String comment, Instant reportedAt) {
        this.line = line;
        this.delayMinutes = delayMinutes;
        this.stopName = stopName;
        this.comment = comment;
        this.reportedAt = reportedAt;
    }

    public Long getId() { return id; }
    public Line getLine() { return line; }
    public int getDelayMinutes() { return delayMinutes; }
    public String getStopName() { return stopName; }
    public String getComment() { return comment; }
    public Instant getReportedAt() { return reportedAt; }
}
