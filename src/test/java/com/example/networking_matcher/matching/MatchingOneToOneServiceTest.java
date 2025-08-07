package com.example.networking_matcher.matching;

import com.example.networking_matcher.excel.ExcelService;
import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.LeadersAndParticipants;
import com.example.networking_matcher.models.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

class MatchingOneToOneServiceTest {

    @Mock
    private ExcelService excelService;

    private MatchingOneToOneService matchingOneToOneService;

    @BeforeEach
    void setUp() {
        this.matchingOneToOneService = new MatchingOneToOneService(excelService);
    }

    @Test
    void matchOneToOne() {
    }

    private LeadersAndParticipants getLeadersAndParticipants() {
        List<Leader> leaders = getListOfLeaders();

        List<Participant> participants = new ArrayList<>();

        return new LeadersAndParticipants(leaders, participants);
    }

    private Leader getLeader(String name, String email, String preference) {
        return new Leader(name, email, preference, new ArrayList<>());
    }

    private List<Leader> getListOfLeaders() {
        List<Leader> leaders = new ArrayList<>();
        leaders.add(avaMontgomery());
        leaders.add(marcusFlynn());
        leaders.add(taliaRivers());
        leaders.add(eliotTran());
        leaders.add(ianMcAllister());

        return leaders;
    }

//    private List<Participant> getListOfParticipants() {
//        List<Leader> leaders = new ArrayList<>();
//        leaders.add(avaMontgomery());
//        leaders.add(marcusFlynn());
//        leaders.add(taliaRivers());
//        leaders.add(eliotTran());
//        leaders.add(ianMcAllister());
//
//        return leaders;
//    }

    private Leader avaMontgomery() {
        return getLeader("Ava Montgomery", "ava.monty@creovault.com", "Finance");
    }

    private Leader marcusFlynn() {
        return getLeader("Marcus Flynn", "marc.flynn@inkspire.net", "Compliance");
    }

    private Leader taliaRivers() {
        return getLeader("Talia Rivers", "talia.r@draftcanvas.com", "HR");
    }

    private Leader eliotTran() {
        return getLeader("Eliot Tran", "eli.tran@designsyndicate.org", "Finance");
    }

    private Leader ianMcAllister() {
        return getLeader("Ian McAllister", "ian.m@brandplot.net", "Compliance");
    }

}