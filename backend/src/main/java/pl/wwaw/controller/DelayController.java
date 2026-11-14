package pl.wwaw.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pl.wwaw.dto.DelayRequest;
import pl.wwaw.dto.DelayResponse;
import pl.wwaw.service.DelayService;


@RestController
@RequestMapping("/api/delays")
public class DelayController {

    private final DelayService delayService;

    public DelayController(DelayService delayService) {
        this.delayService = delayService;
    }

    @GetMapping
    public List<DelayResponse> list(@RequestParam(required = false) Long lineId) {
        return delayService.findRecent(lineId);
    }

    @PostMapping
    public ResponseEntity<DelayResponse> create(@Valid @RequestBody DelayRequest request) {
        DelayResponse created = delayService.create(request);
        return ResponseEntity.created(URI.create("/api/delays/" + created.id())).body(created);
    }
}
