package com.attendance.backend.scheduler;

import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.repository.ClassSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ClassSessionLifecycleScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(ClassSessionLifecycleScheduler.class);

    private final ClassSessionRepository classRepo;

    public ClassSessionLifecycleScheduler(ClassSessionRepository classRepo) {
        this.classRepo = classRepo;
    }

    /**
     * Runs every 1 minute
     * Handles:
     * 1. Auto-activation of future classes
     * 2. Auto-expiration of finished classes
     *
     * Deleted classes are ALWAYS ignored.
     */
    @Scheduled(fixedRate = 60_000)
    public void manageClassLifecycle() {

        LocalDateTime now = LocalDateTime.now();

        // ============================
        // ACTIVATE CLASSES
        // ============================
        List<ClassSession> toActivate =
                classRepo.findClassesToActivate(now);

        if (!toActivate.isEmpty()) {
            toActivate.forEach(c -> {
                if (!c.isDeleted()) {        // 🔒 safety check
                    c.setActive(true);
                }
            });

            classRepo.saveAll(toActivate);
            log.info("Activated {} class(es)", toActivate.size());
        }

        // ============================
        // EXPIRE CLASSES
        // ============================
        List<ClassSession> toExpire =
                classRepo.findClassesToExpire(now);

        if (!toExpire.isEmpty()) {
            toExpire.forEach(c -> {
                if (!c.isDeleted()) {        // 🔒 safety check
                    c.setActive(false);
                }
            });

            classRepo.saveAll(toExpire);
            log.info("Expired {} class(es)", toExpire.size());
        }
    }
}
