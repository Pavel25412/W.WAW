package pl.wwaw.dto;

import pl.wwaw.model.Line;
import pl.wwaw.model.TransportType;

public record LineResponse(Long id, String number, String name, TransportType type) {

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getNumber(), line.getName(), line.getType());
    }
}
