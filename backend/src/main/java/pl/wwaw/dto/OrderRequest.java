package pl.wwaw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotBlank(message = "Brak identyfikatora użytkownika.") @Size(max = 64) String ownerId,
        @NotNull(message = "Wybierz rodzaj biletu.") Long ticketTypeId,
        @NotNull(message = "Podaj liczbę biletów.") @Min(value = 1, message = "Minimum 1 bilet.")
        @Max(value = 10, message = "Maksymalnie 10 biletów w jednym zamówieniu.") Integer quantity) {
}
