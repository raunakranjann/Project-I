package com.attendance.backend.service;

import com.attendance.backend.model.routine.ScheduleConfig;
import com.attendance.backend.model.routine.Timeslot;
import com.attendance.backend.model.routine.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeslotService {

    private final TimeslotRepository timeslotRepo;

    /**
     * SAFE REGENERATION:
     * - Never deletes existing timeslots
     * - Deactivates old ones
     * - Creates fresh active timeslots
     */
    @Transactional
    public void regenerateTimeslots(ScheduleConfig config) {

        // 1️⃣ Deactivate existing timeslots (DO NOT DELETE)
        List<Timeslot> existing = timeslotRepo.findAll();
        for (Timeslot t : existing) {
            t.setActive(false);
        }
        timeslotRepo.saveAll(existing);

        // 2️⃣ Create new active timeslots
        LocalTime current = config.getDayStartTime();

        for (int p = 1; p <= config.getTotalPeriods(); p++) {

            LocalTime start = current;
            LocalTime end = current.plusMinutes(config.getDurationMinutes());

            Timeslot slot = new Timeslot();
            slot.setPeriodNo(p);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setActive(true);

            timeslotRepo.save(slot);

            current = end;
        }
    }
}
