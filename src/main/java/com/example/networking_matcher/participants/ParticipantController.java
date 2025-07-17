package com.example.networking_matcher.participants;

import com.example.networking_matcher.models.LeadersAndParticipants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private LeadersAndParticipants leadersAndParticipants;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("")
    public void getLeadersAndParticipantsFromExcelData() throws IOException {
        leadersAndParticipants = participantService.getParticipants(
                "src/main/resources/static/excel/Colleagues.xlsx",
                "src/main/resources/static/excel/Volunteers.xlsx");

        System.out.println();
    }

    @GetMapping("/one-to-one")
    public void matchParticipantsOneToOne() throws IOException {
        leadersAndParticipants = participantService.getParticipants(
                "src/main/resources/static/excel/Colleagues.xlsx",
                "src/main/resources/static/excel/Volunteers.xlsx");

        participantService.matchOneToOne(leadersAndParticipants);
    }
}
