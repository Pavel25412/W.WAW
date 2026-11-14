package pl.wwaw.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.wwaw.dto.AdvisorResponse;
import pl.wwaw.model.FareType;
import pl.wwaw.service.AdvisorService;

@RestController
@RequestMapping("/api/advisor")
public class AdvisorController {

    private final AdvisorService advisorService;

    public AdvisorController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @GetMapping
    public AdvisorResponse advise(@RequestParam Long lineId,
                                  @RequestParam int plannedMinutes,
                                  @RequestParam(defaultValue = "NORMAL") FareType fare) {
        return advisorService.advise(lineId, plannedMinutes, fare);
    }
}
