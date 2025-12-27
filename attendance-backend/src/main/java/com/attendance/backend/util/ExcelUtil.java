package com.attendance.backend.util;

import com.attendance.backend.dto.AttendanceViewDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ExcelUtil {

    private ExcelUtil() {
        // Utility class
    }

    public static void writeAttendanceExcel(
            List<AttendanceViewDTO> records,
            OutputStream os
    ) throws Exception {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Attendance Report");

            // ======================
            // STYLES
            // ======================
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            DateTimeFormatter dateFmt = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

            // ======================
            // HEADER ROW
            // ======================
            String[] headers = {
                    "Student ID",
                    "Roll No",
                    "Student Name",
                    "Class ID",
                    "Subject",
                    "Teacher",
                    "Date",
                    "Time",
                    "Status"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Freeze header
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0, 0, 0, headers.length - 1
                    )
            );

            // ======================
            // DATA ROWS
            // ======================
            int rowNum = 1;

            for (AttendanceViewDTO a : records) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(
                        a.getUserId() != null ? a.getUserId() : 0
                );

                row.createCell(1).setCellValue(
                        safe(a.getRollNo())
                );

                row.createCell(2).setCellValue(
                        safe(a.getUserName())
                );

                row.createCell(3).setCellValue(
                        a.getClassId() != null ? a.getClassId() : 0
                );

                row.createCell(4).setCellValue(
                        safe(a.getSubjectName())
                );

                row.createCell(5).setCellValue(
                        safe(a.getTeacherName())
                );

                row.createCell(6).setCellValue(
                        a.getDate() != null
                                ? a.getDate().format(dateFmt)
                                : ""
                );

                row.createCell(7).setCellValue(
                        a.getTimestamp() != null
                                ? a.getTimestamp().toLocalTime().format(timeFmt)
                                : ""
                );

                Cell statusCell = row.createCell(8);
                statusCell.setCellValue(safe(a.getStatus()));
                statusCell.setCellStyle(centerStyle);
            }

            // ======================
            // AUTO SIZE
            // ======================
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ======================
            // WRITE
            // ======================
            workbook.write(os);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
