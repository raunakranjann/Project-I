package com.rentconnect.backend.service;

import com.rentconnect.backend.entity.Allocation;
import com.rentconnect.backend.entity.Invoice;
import com.rentconnect.backend.repository.AllocationRepository;
import com.rentconnect.backend.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillingScheduler {

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // Run at 10:00 AM on the 1st day of every month
    // Cron expression: Second Minute Hour Day Month Weekday
  //  @Scheduled(cron = "0 0 10 1 * ?") //for monthly


    @Scheduled(fixedRate = 60000) // for one minute interval


    public void generateMonthlyRent() {
        System.out.println("Scheduler Started: Generating Monthly Rent Invoices...");

        // 1. Fetch all active allocations (Tenants currently living in rooms)
        List<Allocation> activeAllocations = allocationRepository.findAll();
        // Note: In a real app, you'd filter by 'isActive = true'

        for (Allocation allocation : activeAllocations) {
            // 2. Create a Rent Invoice
            Invoice invoice = new Invoice();
            invoice.setAllocation(allocation);
            invoice.setAmount(allocation.getRoom().getBaseRent()); // Just Base Rent
            invoice.setBillDate(LocalDate.now());
            invoice.setDueDate(LocalDate.now().plusDays(5));
            invoice.setStatus("PENDING");

            // Electricity is 0 for now (Landlord adds reading later)
            invoice.setElectricityUsage(0.0);
            invoice.setCurrentReading(0.0);
            invoice.setPreviousReading(0.0);

            invoiceRepository.save(invoice);
            System.out.println("Generated Invoice for User ID: " + allocation.getTenant().getUserId());
        }

        System.out.println("Scheduler Finished.");
    }
}