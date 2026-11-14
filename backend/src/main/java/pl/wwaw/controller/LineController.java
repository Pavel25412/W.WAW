package pl.wwaw.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pl.wwaw.dto.LineRequest;
import pl.wwaw.dto.LineResponse;
import pl.wwaw.service.LineService;

@RestController
@RequestMapping("/api/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public List<LineResponse> list() {
        return lineService.findAll();
    }

    @GetMapping("/{id}")
    public LineResponse get(@PathVariable Long id) {
        return lineService.findById(id);
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@Valid @RequestBody LineRequest request) {
        LineResponse created = lineService.create(request);
        return ResponseEntity.created(URI.create("/api/lines/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public LineResponse update(@PathVariable Long id, @Valid @RequestBody LineRequest request) {
        return lineService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
