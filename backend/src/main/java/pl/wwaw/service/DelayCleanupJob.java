package pl.wwaw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DelayCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(DelayCleanupJob.class);

    private final DelayService delayService;

    public DelayCleanupJob(DelayService delayService) {
        this.delayService = delayService;
    }

    @Scheduled(cron = "${wwaw.delays.cleanup-cron}", zone = "Europe/Warsaw")
    public void removeExpiredReports() {
        int removed = delayService.deleteExpired();
        log.info("Delay journal cleanup: removed {} expired report(s)", removed);
    }
}
