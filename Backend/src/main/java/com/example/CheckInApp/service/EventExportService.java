package com.example.CheckInApp.service;

import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.Registration;
import com.example.CheckInApp.model.RegistrationStatus;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventExportService {

    private static final ZoneId EXPORT_ZONE = ZoneId.of("Europe/Bucharest");
    private static final DateTimeFormatter EXPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public record ExportResult(String eventName, byte[] data) {
    }

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Transactional(readOnly = true)
    public ExportResult exportAttendance(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (event.getStatus() != EventStatus.COMPLETED) {
            throw new IllegalArgumentException("Attendance export is only available for completed events.");
        }

        List<Registration> registrations = registrationRepository.findAllByEventIdWithUser(eventId,
                RegistrationStatus.CONFIRMED);
        Set<Long> checkedInUserIds = attendanceRecordRepository.findCheckedInUserIdsByEventId(eventId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "nr_crt", "lastName", "firstName", "email", "gdpr", "registration_date", "present" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Registration reg : registrations) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(reg.getUser().getLastName());
                row.createCell(2).setCellValue(reg.getUser().getFirstName());
                row.createCell(3).setCellValue(reg.getUser().getEmail());
                row.createCell(4).setCellValue(Boolean.TRUE.equals(reg.getGdprConsent()) ? "yes" : "no");
                row.createCell(5).setCellValue(formatRegistrationDate(reg));
                row.createCell(6).setCellValue(
                        checkedInUserIds.contains(reg.getUser().getId()) ? "yes" : "no");
                rowNum++;
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ExportResult(event.getName(), out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate attendance Excel file.", e);
        }
    }

    private static String formatRegistrationDate(Registration reg) {
        if (reg.getRegistrationDate() == null) {
            return "";
        }
        ZonedDateTime bucharestTime = reg.getRegistrationDate().atZone(EXPORT_ZONE);
        return bucharestTime.format(EXPORT_DATE_FORMAT);
    }
}
