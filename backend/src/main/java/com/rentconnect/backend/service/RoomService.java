package com.rentconnect.backend.service;

import com.rentconnect.backend.dto.RoomDto;
import com.rentconnect.backend.entity.Room;
import com.rentconnect.backend.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // 1. Add a new Room
    public String addRoom(RoomDto roomDto) {
        // Check if room number already exists
        if (roomRepository.existsByRoomNo(roomDto.getRoomNo())) {
            return "Error: Room Number " + roomDto.getRoomNo() + " already exists!";
        }

        Room room = new Room();
        room.setRoomNo(roomDto.getRoomNo());
        room.setFloorNo(roomDto.getFloorNo());
        room.setBaseRent(roomDto.getBaseRent());
        room.setOccupied(false); // Default is empty

        roomRepository.save(room);
        return "Room Added Successfully!";
    }

    // 2. Get all Rooms (For the Dashboard)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}