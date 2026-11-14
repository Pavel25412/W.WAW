package pl.wwaw.dto;

public record AdvisorResponse(Long lineId, String lineNumber, int plannedMinutes, int delayBufferMinutes,
                              int requiredMinutes, boolean basedOnDelayData, boolean upgradedBecauseOfDelays,
                              TicketTypeResponse recommended, String reason) {
}
