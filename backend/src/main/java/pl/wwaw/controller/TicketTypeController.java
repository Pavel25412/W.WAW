package pl.wwaw.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.wwaw.dto.TicketTypeResponse;
import pl.wwaw.model.FareType;
import pl.wwaw.service.TicketTypeService;

@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    public TicketTypeController(TicketTypeService ticketTypeService) {
        this.ticketTypeService = ticketTypeService;
    }

    @GetMapping
    public List<TicketTypeResponse> list(@RequestParam(required = false) FareType fare) {
        return ticketTypeService.findAll(fare);
    }
}
