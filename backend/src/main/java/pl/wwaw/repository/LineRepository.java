package pl.wwaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wwaw.model.Line;
import pl.wwaw.model.TransportType;

public interface LineRepository extends JpaRepository<Line, Long> {

    boolean existsByNumberIgnoreCaseAndType(String number, TransportType type);

    Optional<Line> findByNumberIgnoreCaseAndType(String number, TransportType type);
}
