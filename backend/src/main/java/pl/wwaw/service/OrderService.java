package pl.wwaw.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.dto.OrderRequest;
import pl.wwaw.dto.OrderResponse;
import pl.wwaw.dto.PaymentRequest;
import pl.wwaw.dto.TicketResponse;
import pl.wwaw.exception.ConflictException;
import pl.wwaw.exception.NotFoundException;
import pl.wwaw.exception.PaymentDeclinedException;
import pl.wwaw.model.OrderStatus;
import pl.wwaw.model.Ticket;
import pl.wwaw.model.TicketOrder;
import pl.wwaw.model.TicketType;
import pl.wwaw.payment.PaymentProvider;
import pl.wwaw.payment.PaymentResult;
import pl.wwaw.repository.TicketOrderRepository;
import pl.wwaw.repository.TicketRepository;
import pl.wwaw.repository.TicketTypeRepository;

@Service
public class OrderService {

    private final TicketOrderRepository orders;
    private final TicketRepository tickets;
    private final TicketTypeRepository ticketTypes;
    private final PaymentProvider paymentProvider;
    private final Clock clock;

    public OrderService(TicketOrderRepository orders, TicketRepository tickets, TicketTypeRepository ticketTypes,
                        PaymentProvider paymentProvider, Clock clock) {
        this.orders = orders;
        this.tickets = tickets;
        this.ticketTypes = ticketTypes;
        this.paymentProvider = paymentProvider;
        this.clock = clock;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        TicketType type = ticketTypes.findById(request.ticketTypeId())
                .orElseThrow(() -> new NotFoundException(
                        "Nie znaleziono rodzaju biletu o id " + request.ticketTypeId() + "."));
        TicketOrder order = orders.save(
                new TicketOrder(request.ownerId(), type, request.quantity(), Instant.now(clock)));
        return OrderResponse.from(order, List.of());
    }

    @Transactional
    public OrderResponse pay(Long orderId, PaymentRequest request) {
        TicketOrder order = orders.findById(orderId)
                .filter(o -> o.getOwnerId().equals(request.ownerId()))
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zamówienia o id " + orderId + "."));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ConflictException("To zamówienie zostało już opłacone.");
        }

        PaymentResult result = paymentProvider.charge(order.getTotalAmount(), request.method(), request.blikCode());
        if (!result.approved()) {
            throw new PaymentDeclinedException(result.message());
        }

        Instant now = Instant.now(clock);
        order.markPaid(result.reference(), now);
        List<Ticket> issued = tickets.saveAll(
                IntStream.range(0, order.getQuantity()).mapToObj(i -> new Ticket(order, now)).toList());
        List<TicketResponse> responses = issued.stream().map(t -> TicketResponse.from(t, now)).toList();
        return OrderResponse.from(order, responses);
    }
}
