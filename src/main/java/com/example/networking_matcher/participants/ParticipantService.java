package com.example.networking_matcher.participants;

import com.example.networking_matcher.excel.ExcelService;
import com.example.networking_matcher.matching.MatchingOneToOneService;
import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.LeadersAndParticipants;
import com.example.networking_matcher.models.OneToOneMatch;
import com.example.networking_matcher.models.Participant;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class ParticipantService {

    private final ExcelService excelService;
    private final MatchingOneToOneService matchingOneToOneService;

    public ParticipantService(ExcelService excelService, MatchingOneToOneService matchingOneToOneService) {
        this.excelService = excelService;
        this.matchingOneToOneService = matchingOneToOneService;
    }

    public LeadersAndParticipants getParticipants(String participantExcel, String leaderExcel) throws IOException {
        try {
            List<Participant> participants = excelService.getParticipantsFromSpreadsheet(participantExcel);
            List<Leader> leaders = excelService.getLeadersFromSpreadsheet(leaderExcel);
            return new LeadersAndParticipants(leaders, participants);

        } catch (IOException ex) {
            throw new IOException("Unexpected error occurred while processing participant data: " + ex.getMessage());
        }
    }

    public void getOneToOneMatches(LeadersAndParticipants leadersAndParticipants) {
        HashMap<String, List<OneToOneMatch>> oneToOneMatches = matchingOneToOneService.matchOneToOne(leadersAndParticipants);
        excelService.createExcelNotebook(oneToOneMatches);
    }
}
