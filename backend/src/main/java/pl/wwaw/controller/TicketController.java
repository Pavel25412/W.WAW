package pl.wwaw.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.wwaw.dto.TicketResponse;
import pl.wwaw.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<TicketResponse> list(@RequestParam String ownerId) {
        return ticketService.findByOwner(ownerId);
    }

    @PostMapping("/{id}/validate")
    public TicketResponse validate(@PathVariable Long id, @RequestParam String ownerId) {
        return ticketService.validate(id, ownerId);
    }
}
