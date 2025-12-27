package com.attendance.backend.scheduler;

import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClassSessionLifecycleScheduler {

    private final ClassSessionRepository classRepo;

    // =====================================================
    // ACTIVATE LIVE CLASSES (EVERY 1 MIN)
    // =====================================================
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void activateClasses() {

        LocalDateTime now = LocalDateTime.now();

        List<ClassSession> toActivate =
                classRepo.findClassesToActivate(now);

        for (ClassSession c : toActivate) {
            c.setActive(true);
            log.info("Activated class {}", c.getId());
        }

        classRepo.saveAll(toActivate);
    }

    // =====================================================
    // EXPIRE FINISHED CLASSES (EVERY 1 MIN)
    // =====================================================
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void expireClasses() {

        LocalDateTime now = LocalDateTime.now();

        List<ClassSession> toExpire =
                classRepo.findClassesToExpire(now);

        for (ClassSession c : toExpire) {
            c.setActive(false);
            log.info("Expired class {}", c.getId());
        }

        classRepo.saveAll(toExpire);
    }
}
