package pl.wwaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wwaw.model.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

    List<TicketType> findAllByOrderByValidityMinutesAscFareAsc();
}
