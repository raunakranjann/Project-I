package com.rentconnect.backend.service;

import com.rentconnect.backend.dto.InvoiceRequestDto;
import com.rentconnect.backend.entity.Allocation;
import com.rentconnect.backend.entity.Invoice;
import com.rentconnect.backend.repository.AllocationRepository;
import com.rentconnect.backend.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    public String generateInvoice(InvoiceRequestDto dto) {
        // 1. Fetch Allocation Details
        Allocation allocation = allocationRepository.findById(dto.getAllocationId())
                .orElseThrow(() -> new RuntimeException("Allocation not found!"));

        // 2. Determine Previous Reading (Smart Logic)
        Double previousReading = 0.0;
        Optional<Invoice> lastInvoice = invoiceRepository.findLastInvoiceByAllocationId(dto.getAllocationId());

        if (lastInvoice.isPresent()) {
            previousReading = lastInvoice.get().getCurrentReading();
        }

        // 3. Calculate Electricity Cost
        // Formula: (Current - Previous) * Rate
        Double unitsConsumed = dto.getCurrentMeterReading() - previousReading;
        Double electricityCost = unitsConsumed * dto.getUnitRate();

        // 4. Calculate Total Bill (Rent + Electricity)
        Double baseRent = allocation.getRoom().getBaseRent();
        Double totalAmount = baseRent + electricityCost;

        // 5. Create and Save Invoice
        Invoice invoice = new Invoice();
        invoice.setAllocation(allocation);
        invoice.setPreviousReading(previousReading);
        invoice.setCurrentReading(dto.getCurrentMeterReading());
        invoice.setElectricityUsage(unitsConsumed);
        invoice.setAmount(totalAmount);
        invoice.setBillDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(5)); // Due in 5 days
        invoice.setStatus("PENDING");

        invoiceRepository.save(invoice);

        return "Invoice Generated! Total: " + totalAmount + " (Rent: " + baseRent + " + Elec: " + electricityCost + ")";
    }
}