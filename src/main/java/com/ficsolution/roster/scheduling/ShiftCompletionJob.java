package com.ficsolution.roster.scheduling;

import com.ficsolution.roster.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShiftCompletionJob {

    private final ShiftService shiftService;


    @Scheduled(cron = "${roster.jobs.complete-shifts.cron}", zone = "${roster.time-zone}")
    void updateToCompletedShift() {
        int shiftsUpdated = shiftService.completePastShifts();
        log.info("{} shifts marked as COMPLETED", shiftsUpdated);
    }
}
