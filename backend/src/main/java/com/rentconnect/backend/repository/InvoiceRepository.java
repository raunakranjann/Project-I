package com.rentconnect.backend.repository;

import com.rentconnect.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Custom SQL query to find the latest invoice for a specific allocation
    @Query(value = "SELECT * FROM invoices WHERE allocation_id = :allocationId ORDER BY bill_date DESC LIMIT 1", nativeQuery = true)
    Optional<Invoice> findLastInvoiceByAllocationId(Long allocationId);
}