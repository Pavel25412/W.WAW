package pl.wwaw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.wwaw.model.TransportType;

public record LineRequest(
        @NotBlank(message = "Podaj numer linii.") @Size(max = 10, message = "Numer może mieć maksymalnie 10 znaków.") String number,
        @NotBlank(message = "Podaj nazwę trasy.") @Size(max = 100, message = "Nazwa może mieć maksymalnie 100 znaków.") String name,
        @NotNull(message = "Wybierz rodzaj transportu.") TransportType type) {
}
