package vn.elca.training.pilot_project_back.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import vn.elca.training.pilot_project_back.service.SalaryService;

import java.time.Instant;

@Configuration
@ConditionalOnProperty(name = "schedule.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SchedulerConfig {
    @Autowired
    private SalaryService salaryService;

    @Scheduled(cron = "${salary.csv.process.cron}")
    private void processSalaryCsvFiles() {
        salaryService.processSalaryCsvFilesJob();
        log.info("processSalaryCsvFilesJob at {}", Instant.now());
    }
}
