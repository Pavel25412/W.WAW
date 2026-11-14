package pl.wwaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.wwaw.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
}
