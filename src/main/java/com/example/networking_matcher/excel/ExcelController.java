package com.example.networking_matcher.excel;

import com.example.networking_matcher.models.Participant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("excel")
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("")
    public void run() throws IOException {
        List<Participant> participant = excelService.getParticipantsFromSpreadsheet("src/main/resources/static/excel/Colleagues.xlsx");
        System.out.println();
    }
}
