package com.rentconnect.backend.controller;

import com.rentconnect.backend.dto.AllocationDto;
import com.rentconnect.backend.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/allocations")
@CrossOrigin(origins = "*")
public class AllocationController {

    @Autowired
    private AllocationService allocationService;

    @PostMapping("/create")
    public String createAllocation(@RequestBody AllocationDto dto) {
        return allocationService.createAllocation(dto);
    }
}