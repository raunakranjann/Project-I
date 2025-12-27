package com.attendance.backend.service;

import com.attendance.backend.model.routine.WeeklySchedule;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoutinePdfService {

    public void generateRoutinePdf(
            List<WeeklySchedule> routines,
            int totalPeriods,
            OutputStream os
    ) throws Exception {

        // =================================================
        // BUILD FIXED GRID (Day → Period → Routine)
        // =================================================
        Map<DayOfWeek, Map<Integer, WeeklySchedule>> grid =
                new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            grid.put(day, new HashMap<>());
        }

        for (WeeklySchedule r : routines) {
            grid.get(r.getDayOfWeek()).put(r.getPeriodNo(), r);
        }

        // =================================================
        // PDF SETUP
        // =================================================
        Document document =
                new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

        PdfWriter.getInstance(document, os);
        document.open();

        // =================================================
        // HEADER INFO (SAFE)
        // =================================================
        if (!routines.isEmpty()) {
            WeeklySchedule first = routines.get(0);

            Paragraph meta = new Paragraph(
                    "Course: " + first.getCourse().getName()
                            + " | Branch: " + first.getBranch().getName()
                            + " | Session: " + first.getAcademicSession().getDisplayName()
                            + " | Semester: " + first.getSemester().getNumber(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
            );
            meta.setAlignment(Element.ALIGN_CENTER);
            document.add(meta);
            document.add(Chunk.NEWLINE);
        }

        Paragraph title = new Paragraph(
                "Weekly Class Routine",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
        );
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // =================================================
        // TABLE (FIXED GRID)
        // =================================================
        PdfPTable table = new PdfPTable(totalPeriods + 1);
        table.setWidthPercentage(100);

        float[] widths = new float[totalPeriods + 1];
        widths[0] = 80f;
        for (int i = 1; i <= totalPeriods; i++) {
            widths[i] = 140f;
        }
        table.setWidths(widths);

        Font headerFont =
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font cellFont =
                FontFactory.getFont(FontFactory.HELVETICA, 9);

        // =================================================
        // HEADER ROW
        // =================================================
        table.addCell(headerCell("Day", headerFont));
        for (int p = 1; p <= totalPeriods; p++) {
            table.addCell(headerCell("Period " + p, headerFont));
        }

        // =================================================
        // BODY ROWS (NULL-SAFE)
        // =================================================
        for (DayOfWeek day : DayOfWeek.values()) {

            table.addCell(dayCell(day.name(), headerFont));

            for (int p = 1; p <= totalPeriods; p++) {

                WeeklySchedule routine = grid.get(day).get(p);

                if (routine == null) {
                    table.addCell(emptyCell());
                    continue;
                }

                StringBuilder cellText = new StringBuilder();

                // ✅ SUBJECT (ALWAYS SHOWN)
                if (routine.getSubjectName() != null) {
                    cellText.append(routine.getSubjectName());
                }

                // ✅ TEACHER (OPTIONAL)
                if (routine.getTeacher() != null) {
                    cellText.append("\n(")
                            .append(routine.getTeacher().getName())
                            .append(")");
                }

                table.addCell(
                        dataCell(cellText.toString(), cellFont)
                );
            }
        }

        document.add(table);
        document.close();
    }

    // =================================================
    // CELL HELPERS
    // =================================================
    private PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell dayCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell dataCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setMinimumHeight(45);
        return cell;
    }

    private PdfPCell emptyCell() {
        PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setMinimumHeight(45);
        return cell;
    }
}
