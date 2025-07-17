package com.example.networking_matcher.excel;

import com.example.networking_matcher.excel.exceptions.ExcelException;
import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.Participant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
