package pl.wwaw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.dto.TicketTypeResponse;
import pl.wwaw.model.FareType;
import pl.wwaw.repository.TicketTypeRepository;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypes;

    public TicketTypeService(TicketTypeRepository ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    @Transactional(readOnly = true)
    public List<TicketTypeResponse> findAll(FareType fare) {
        return ticketTypes.findAllByOrderByValidityMinutesAscFareAsc().stream()
                .filter(t -> fare == null || t.getFare() == fare)
                .map(TicketTypeResponse::from)
                .toList();
    }
}
