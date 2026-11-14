package pl.wwaw.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.wwaw.dto.LineRequest;
import pl.wwaw.dto.LineResponse;
import pl.wwaw.exception.ConflictException;
import pl.wwaw.exception.NotFoundException;
import pl.wwaw.model.Line;
import pl.wwaw.repository.DelayReportRepository;
import pl.wwaw.repository.LineRepository;

@Service
public class LineService {

    private final LineRepository lines;
    private final DelayReportRepository delays;

    public LineService(LineRepository lines, DelayReportRepository delays) {
        this.lines = lines;
        this.delays = delays;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return findAllEntities().stream().map(LineResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<Line> findAllEntities() {
        return lines.findAll(Sort.by("type", "number"));
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Line getEntity(Long id) {
        return lines.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono linii o id " + id + "."));
    }

    @Transactional
    public LineResponse create(LineRequest request) {
        String number = normalize(request.number());
        if (lines.existsByNumberIgnoreCaseAndType(number, request.type())) {
            throw new ConflictException("Linia " + number + " (" + request.type() + ") już istnieje.");
        }
        return LineResponse.from(lines.save(new Line(number, request.name().trim(), request.type())));
    }

    @Transactional
    public LineResponse update(Long id, LineRequest request) {
        Line line = getEntity(id);
        String number = normalize(request.number());
        lines.findByNumberIgnoreCaseAndType(number, request.type())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("Linia " + number + " (" + request.type() + ") już istnieje.");
                });
        line.setNumber(number);
        line.setName(request.name().trim());
        line.setType(request.type());
        return LineResponse.from(line);
    }

    @Transactional
    public void delete(Long id) {
        Line line = getEntity(id);
        delays.deleteByLineId(id);
        lines.delete(line);
    }

    private static String normalize(String number) {
        return number.trim().toUpperCase(Locale.ROOT);
    }
}
