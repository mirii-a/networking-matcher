package com.example.networking_matcher.matching;

import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.LeadersAndParticipants;
import com.example.networking_matcher.models.OneToOneMatches;
import com.example.networking_matcher.models.Participant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class MatchingOneToOneService {

    public MatchingOneToOneService() {
    }

    public void matchOneToOne(LeadersAndParticipants leadersAndParticipants) {
        List<Leader> leaders = leadersAndParticipants.leaders();
        List<Participant> participants = leadersAndParticipants.participants();

        HashSet<String> preferences = new HashSet<>();

        leaders.forEach(leader -> preferences.add(leader.preference()));
        participants.forEach(participant -> preferences.add(participant.preference()));

        List<OneToOneMatches> matches = new ArrayList<>();
        List<Participant> unmatchedLeaders = new ArrayList<>();
        List<Participant> unmatchedParticipants = new ArrayList<>();

        for (String preference : preferences) {
            List<Leader> leadersWithPreference = leaders.stream()
                    .filter(leader -> leader.preference().equals(preference))
                    .toList();
            List<Participant> participantsWithPreference = participants.stream()
                    .filter(participant -> participant.preference().equals(preference))
                    .toList();



            System.out.println();
        }

    }


}
