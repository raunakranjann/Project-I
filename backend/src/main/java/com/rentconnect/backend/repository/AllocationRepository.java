package com.rentconnect.backend.repository;

import com.rentconnect.backend.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    // Find allocations for a specific user (so a tenant sees only their room)
    List<Allocation> findByTenant_UserId(Long userId);
}