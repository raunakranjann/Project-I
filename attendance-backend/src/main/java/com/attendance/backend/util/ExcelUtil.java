package com.attendance.backend.util;

import com.attendance.backend.dto.AttendanceViewDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

public class ExcelUtil {

    private ExcelUtil() {
        // utility class
    }

    public static void writeAttendanceExcel(
            List<AttendanceViewDTO> records,
            OutputStream os
    ) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Report");

        // ======================
        // HEADER STYLE
        // ======================
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // ======================
        // HEADER ROW
        // ======================
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "User ID",
                "Roll No",
                "Student Name",
                "Class ID",
                "Subject",
                "Teacher",
                "Date",
                "Time",
                "Status"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

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
                    a.getRollNo() != null ? a.getRollNo() : ""
            );
            row.createCell(2).setCellValue(
                    a.getUserName() != null ? a.getUserName() : ""
            );
            row.createCell(3).setCellValue(
                    a.getClassId() != null ? a.getClassId() : 0
            );
            row.createCell(4).setCellValue(
                    a.getSubjectName() != null ? a.getSubjectName() : ""
            );
            row.createCell(5).setCellValue(
                    a.getTeacherName() != null ? a.getTeacherName() : ""
            );
            row.createCell(6).setCellValue(
                    a.getDate() != null ? a.getDate().toString() : ""
            );
            row.createCell(7).setCellValue(
                    a.getTimestamp() != null ? a.getTimestamp().toString() : ""
            );
            row.createCell(8).setCellValue(
                    a.getStatus() != null ? a.getStatus() : ""
            );
        }

        // ======================
        // WRITE FILE
        // ======================
        workbook.write(os);
        workbook.close();
    }
}
