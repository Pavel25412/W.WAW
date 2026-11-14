package pl.wwaw.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import pl.wwaw.model.OrderStatus;
import pl.wwaw.model.TicketOrder;

public record OrderResponse(Long id, OrderStatus status, int quantity, BigDecimal totalAmount,
                            TicketTypeResponse ticketType, Instant createdAt, Instant paidAt,
                            List<TicketResponse> tickets) {

    public static OrderResponse from(TicketOrder o, List<TicketResponse> tickets) {
        return new OrderResponse(o.getId(), o.getStatus(), o.getQuantity(), o.getTotalAmount(),
                TicketTypeResponse.from(o.getTicketType()), o.getCreatedAt(), o.getPaidAt(), tickets);
    }
}
