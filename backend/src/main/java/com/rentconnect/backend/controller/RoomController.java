package com.rentconnect.backend.controller;

import com.rentconnect.backend.dto.RoomDto;
import com.rentconnect.backend.entity.Room;
import com.rentconnect.backend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // POST: Add a new room
    @PostMapping("/add")
    public String addRoom(@RequestBody RoomDto roomDto) {
        return roomService.addRoom(roomDto);
    }

    // GET: View all rooms
    @GetMapping("/all")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
}