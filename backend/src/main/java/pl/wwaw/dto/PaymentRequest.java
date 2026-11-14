package pl.wwaw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.wwaw.payment.PaymentMethod;

public record PaymentRequest(
        @NotBlank(message = "Brak identyfikatora użytkownika.") @Size(max = 64) String ownerId,
        @NotNull(message = "Wybierz metodę płatności.") PaymentMethod method,
        @Size(max = 6, message = "Kod BLIK ma 6 cyfr.") String blikCode) {
}
