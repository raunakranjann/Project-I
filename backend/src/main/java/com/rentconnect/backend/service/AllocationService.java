package com.rentconnect.backend.service;

import com.rentconnect.backend.dto.AllocationDto;
import com.rentconnect.backend.entity.Allocation;
import com.rentconnect.backend.entity.Room;
import com.rentconnect.backend.entity.User;
import com.rentconnect.backend.repository.AllocationRepository;
import com.rentconnect.backend.repository.RoomRepository;
import com.rentconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllocationService {

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public String createAllocation(AllocationDto dto) {
        // 1. Fetch the Room
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found!"));

        // 2. Check if Room is already occupied
        if (room.isOccupied()) {
            return "Error: Room " + room.getRoomNo() + " is already occupied!";
        }

        // 3. Fetch the Tenant
        User tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found!"));

        // 4. Create the Allocation
        Allocation allocation = new Allocation();
        allocation.setRoom(room);
        allocation.setTenant(tenant);
        allocation.setStartDate(dto.getStartDate());
        allocation.setEndDate(dto.getEndDate());
        allocation.setActive(true);

        allocationRepository.save(allocation);

        // 5. CRITICAL: Update Room Status to Occupied
        room.setOccupied(true);
        roomRepository.save(room);

        return "Room Allocated Successfully to " + tenant.getName();
    }
}