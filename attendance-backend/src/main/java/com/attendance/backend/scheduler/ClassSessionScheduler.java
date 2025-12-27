package com.attendance.backend.scheduler;

import com.attendance.backend.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClassSessionScheduler {

    private final ClassSessionService classSessionService;

    // =====================================================
    // DAILY GENERATION (00:05 AM)
    // =====================================================
    @Scheduled(cron = "0 5 0 * * *")
    public void generateTodayClasses() {

        LocalDate today = LocalDate.now();
        log.info("Generating class sessions for {}", today);

        classSessionService.generateClassesForDate(today);
    }
}
