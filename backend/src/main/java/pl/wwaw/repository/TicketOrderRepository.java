package pl.wwaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wwaw.model.TicketOrder;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {
}
