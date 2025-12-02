package com.rentconnect.backend.controller;

import com.rentconnect.backend.dto.InvoiceRequestDto;
import com.rentconnect.backend.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/generate")
    public String generateInvoice(@RequestBody InvoiceRequestDto dto) {
        return invoiceService.generateInvoice(dto);
    }
}