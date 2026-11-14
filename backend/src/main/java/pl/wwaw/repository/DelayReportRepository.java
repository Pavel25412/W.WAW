package pl.wwaw.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pl.wwaw.model.DelayReport;

public interface DelayReportRepository extends JpaRepository<DelayReport, Long> {

    List<DelayReport> findByReportedAtAfterOrderByReportedAtDesc(Instant cutoff);

    List<DelayReport> findByLineIdAndReportedAtAfterOrderByReportedAtDesc(Long lineId, Instant cutoff);

    List<DelayReport> findByReportedAtAfter(Instant cutoff);

    List<DelayReport> findByLineIdAndReportedAtAfter(Long lineId, Instant cutoff);

    @Modifying
    @Query("delete from DelayReport d where d.reportedAt <= :cutoff")
    int deleteExpired(@Param("cutoff") Instant cutoff);

    @Modifying
    @Query("delete from DelayReport d where d.line.id = :lineId")
    int deleteByLineId(@Param("lineId") Long lineId);
}
