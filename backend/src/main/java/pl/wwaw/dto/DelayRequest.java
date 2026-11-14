package pl.wwaw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DelayRequest(
        @NotNull(message = "Wybierz linię.") Long lineId,
        @NotNull(message = "Podaj opóźnienie w minutach.") @Min(value = 1, message = "Minimum 1 minuta.")
        @Max(value = 180, message = "Maksymalnie 180 minut.") Integer delayMinutes,
        @Size(max = 80, message = "Nazwa przystanku może mieć maksymalnie 80 znaków.") String stopName,
        @Size(max = 200, message = "Komentarz może mieć maksymalnie 200 znaków.") String comment) {
}
