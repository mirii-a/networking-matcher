package com.example.networking_matcher.matching;

import com.example.networking_matcher.TestObjects.TestObjects;
import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.LeadersAndParticipants;
import com.example.networking_matcher.models.OneToOneMatch;
import com.example.networking_matcher.models.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

class MatchingOneToOneServiceTest {

    private final TestObjects helper = new TestObjects();
    private MatchingOneToOneService matchingOneToOneService;

    @BeforeEach
    void setUp() {
        this.matchingOneToOneService = new MatchingOneToOneService();
    }

    @Test
    void matchOneToOne() throws Exception {
        HashMap<String, List<OneToOneMatch>> result = matchingOneToOneService.matchOneToOne(getLeadersAndParticipants());

    }

    private LeadersAndParticipants getLeadersAndParticipants() {
        List<Leader> leaders = helper.getListOfLeaders();
        List<Participant> participants = helper.getListOfParticipants();

        return new LeadersAndParticipants(leaders, participants);
    }


}