package com.rentconnect.backend.repository;

import com.rentconnect.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Use this to check if a room exists before adding it
    boolean existsByRoomNo(String roomNo);
}