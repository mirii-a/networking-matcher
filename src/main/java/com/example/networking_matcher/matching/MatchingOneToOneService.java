package com.example.networking_matcher.matching;

import com.example.networking_matcher.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class MatchingOneToOneService {

    public MatchingOneToOneService() {
    }

    public void matchOneToOne(LeadersAndParticipants leadersAndParticipants) throws Exception {
        List<Leader> leaders = leadersAndParticipants.leaders();
        List<Participant> participants = leadersAndParticipants.participants();

        int numberOfSlots = 3;

        HashSet<String> preferences = new HashSet<>();

        leaders.forEach(leader -> preferences.add(leader.preference()));
        participants.forEach(participant -> preferences.add(participant.preference()));

        HashMap<String, List<OneToOneMatch>> finalMatches = new HashMap<>();


        for (int i = 0; i < numberOfSlots; i++) {
            String slot = "slot" + i;
            List<OneToOneMatch> matches = new ArrayList<>();
//            List<Participant> matchedParticipants = new ArrayList<>();
//            List<Leader> matchedLeaders = new ArrayList<>();
            List<Leader> unmatchedLeaders = new ArrayList<>();
            List<Participant> unmatchedParticipants = new ArrayList<>();
            for (String preference : preferences) {
                List<Leader> leadersWithPreference = leaders.stream()
                        .filter(leader -> leader.preference().equals(preference))
                        .toList();
                List<Participant> participantsWithPreference = participants.stream()
                        .filter(participant -> participant.preference().equals(preference))
                        .toList();

                List<Leader> matchedLeaders = new ArrayList<>();
                List<Participant> matchedParticipants = new ArrayList<>();

                int numberOfParticipantsInBatch = participantsWithPreference.size();
                int numberOfLeadersInBatch = leadersWithPreference.size();

                if (numberOfLeadersInBatch == 0) {
                    unmatchedParticipants.addAll(participantsWithPreference);
                }
                if (numberOfParticipantsInBatch == 0) {
                    unmatchedLeaders.addAll(leadersWithPreference);
                }

                for (Leader leader : leadersWithPreference) {
                    for (Participant participant : participantsWithPreference) {
                        String leaderEmail = leader.email();
                        String participantEmail = participant.email();
                        if (matchedParticipants.stream().anyMatch(matchedParticipant -> matchedParticipant.email().equals(participantEmail))) {
                            // Continue as participant has been matched
                            continue;
                        }
                        // If participant has not been matched with Leader in previous slot

                        boolean hasLeaderBeenMatched = matchedLeaders.stream().anyMatch(matchedLeader -> matchedLeader.email().equals(leaderEmail));

                        if (participant.matchedWith().stream().noneMatch(match -> match.leaderMatchedWith().email().equals(leaderEmail))) {
                            // Update Participant matchedWith
                            ArrayList<ParticipantMatch> participantMatchedWith = participant.matchedWith();
                            participantMatchedWith.add(new ParticipantMatch(slot, new LeaderDto(leader.name(), leader.email(), leader.preference())));
                            participant = participant.updateMatchedWith(participantMatchedWith);
                            matchedParticipants.add(participant);

                            // Update Leader matchedWith
                            ArrayList<ParticipantDto> participantList = new ArrayList<>();
                            participantList.add(new ParticipantDto(participant.name(), participant.email(), participant.preference()));
                            LeaderMatch leaderMatch = new LeaderMatch(slot, participantList);
                            ArrayList<LeaderMatch> leaderMatchedWith = leader.matchedWith();
                            leaderMatchedWith.add(leaderMatch);
                            leader = leader.updateMatchedWith(leaderMatchedWith);
                            matchedLeaders.add(leader);

                            matches.add(new OneToOneMatch(leader, participant));

                            numberOfLeadersInBatch--;
                            numberOfParticipantsInBatch--;
                            break;
                        }
                    }

                    if (numberOfLeadersInBatch == 0) {
                        // Get remaining participants and add them to the unmatched
                        List<Participant> unmatched = new ArrayList<>(participantsWithPreference);
                        unmatched.removeAll(matchedParticipants);
                        unmatchedParticipants.addAll(unmatched);
                        break;
                    }
                    if (numberOfParticipantsInBatch == 0) {
                        // Get remaining leaders and add them to the unmatched
                        List<Leader> unmatched = new ArrayList<>(leadersWithPreference);
                        unmatched.removeAll(matchedLeaders);
                        unmatchedLeaders.addAll(unmatched);
                        break;
                    }
                }

                System.out.println();
            }
            System.out.println();
//            for ()
            i++;
        }
        System.out.println();

    }


}
