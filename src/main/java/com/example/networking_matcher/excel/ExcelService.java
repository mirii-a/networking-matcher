package com.example.networking_matcher.excel;

import com.example.networking_matcher.excel.exceptions.ExcelException;
import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.OneToOneMatch;
import com.example.networking_matcher.models.Participant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    public ExcelService() {

    }

    public List<Participant> getParticipantsFromSpreadsheet(String fileLocation) throws IOException {
        try {
            Map<Integer, List<String>> data = getDataFromSpreadsheet(fileLocation);

            List<Participant> participants = new ArrayList<>();

            for (List<String> participant : data.values()) {
                String name = participant.get(0);
                String email = participant.get(1);
                String preference = participant.get(2);
                Participant newParticipant = new Participant(name, email, preference);
                participants.add(newParticipant);
            }

            return participants;
        } catch (IOException e) {
            throw new IOException("Unable to process Excel Spreadsheet provided. Please review the spreadsheet and try again.");
        }
    }

    public List<Leader> getLeadersFromSpreadsheet(String fileLocation) throws IOException {
        try {
            Map<Integer, List<String>> data = getDataFromSpreadsheet(fileLocation);

            List<Leader> leaders = new ArrayList<>();

            for (List<String> participant : data.values()) {
                String name = participant.get(0);
                String email = participant.get(1);
                String preference = participant.get(2);
                Leader newLeader = new Leader(name, email, preference);
                leaders.add(newLeader);
            }

            return leaders;
        } catch (IOException e) {
            throw new IOException("Unable to process Excel Spreadsheet provided. Please review the spreadsheet and try again.");
        }
    }

    private Map<Integer, List<String>> getDataFromSpreadsheet(String fileLocation) throws IOException {
        FileInputStream file = new FileInputStream(new File(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);

        return getDataFromSheet(sheet);
    }

    private Map<Integer, List<String>> getDataFromSheet(Sheet sheet) {
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            if (i == 0) {
                i++; // Skipping the header
                continue;
            }
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                if (cell.getCellType() != CellType.STRING) {
                    throw new ExcelException("Unexpected cell value found in Excel spreadsheet: " + cell.getCellType()
                            + "\nRow: " + row
                            + "\nCell: " + cell
                            + "\nExpecting spreadsheet to be names of volunteers or participants only.");
                }
                data.get(i).add(cell.getRichStringCellValue().getString());
            }
            i++;
        }
        return data;
    }

    public void createExcelNotebook(HashMap<String, List<OneToOneMatch>> finalMatches) {
        Workbook workbook = new XSSFWorkbook();

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(false);

        List<String> correctKeyOrder = new ArrayList<>(finalMatches.keySet());
        Collections.reverse(correctKeyOrder);

        for (String slot : correctKeyOrder) {
            List<OneToOneMatch> matches = finalMatches.get(slot);
            System.out.println("===========================================");

            // Create sheet
            Sheet spreadsheet = workbook.createSheet(slot);

            // Set headers
            Row header = spreadsheet.createRow(0);
            setHeaderRowForOneToOne(header, style);

            System.out.println("SLOT: " + slot);

            for (int i = 0; i < matches.size(); i++) {
                System.out.println("Leader:\t" + matches.get(i).leader().name() + "\t" + matches.get(i).leader().email() + "\t" + matches.get(i).leader().preference()
                        + " has been matched with Participant:\t" + matches.get(i).participant().name() + "\t" + matches.get(i).participant().email() + "\t" + matches.get(i).participant().preference());
                Row row = spreadsheet.createRow(i + 1);
                setCellsForRowOneToOne(row, style, matches.get(i).leader(), matches.get(i).participant());

            }
            System.out.println("===========================================");
        }

        try {
            File currentDirectory = new File(".");
            String path = currentDirectory.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + "one-to-one-matches.xlsx";

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new ExcelException("Unable to save matches to Excel spreadsheet. Please try again. Error: " + e.getMessage());
        }

    }

    private void setHeaderRowForOneToOne(Row header, CellStyle style) {
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Leader Name");
        headerCell.setCellStyle(style);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Leader Email");
        headerCell.setCellStyle(style);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Leader Preference");
        headerCell.setCellStyle(style);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Participant Name");
        headerCell.setCellStyle(style);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Participant Email");
        headerCell.setCellStyle(style);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Participant Preference");
        headerCell.setCellStyle(style);
    }

    private void setCellsForRowOneToOne(Row row, CellStyle style, Leader leader, Participant participant) {
        Cell cell = row.createCell(0); // Leader name
        cell.setCellValue(leader.name());
        cell.setCellStyle(style);

        cell = row.createCell(1); // Leader email
        cell.setCellValue(leader.email());
        cell.setCellStyle(style);

        cell = row.createCell(2); // Leader preference
        cell.setCellValue(leader.preference());
        cell.setCellStyle(style);

        cell = row.createCell(3); // Participant name
        cell.setCellValue(participant.name());
        cell.setCellStyle(style);

        cell = row.createCell(4); // Participant email
        cell.setCellValue(participant.email());
        cell.setCellStyle(style);

        cell = row.createCell(5); // Participant preference
        cell.setCellValue(participant.preference());
        cell.setCellStyle(style);

    }
}
