package com.example.networking_matcher.matching;

import com.example.networking_matcher.models.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchingOneToOneService {

    public MatchingOneToOneService() {
    }

    public void matchOneToOne(LeadersAndParticipants leadersAndParticipants) throws Exception {

//        int i = 1;
//
//        while (i > 0) {
//            Scanner myObj = new Scanner(System.in);
//            System.out.println("Input + or -");
//
//            String answer = myObj.nextLine();
//            if (Objects.equals(answer, "-")) {
//                i --;
//            }
//            if (Objects.equals(answer, "+")) {
//                i++;
//            }
//        }
//
//        System.out.println("This seems to work");

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

                while (numberOfLeadersInBatch != 0) {
                    for (Leader leader : leadersWithPreference) {
                        String leaderEmail = leader.email();
                        if (matchedLeaders.stream().anyMatch(matchedLeader -> matchedLeader.email().equals(leaderEmail))) {
                            // Continue as leader has been matched
                            continue;
                        }
                        for (Participant participant : participantsWithPreference) {
                            String participantEmail = participant.email();
                            if (matchedParticipants.stream().anyMatch(matchedParticipant -> matchedParticipant.email().equals(participantEmail))) {
                                // Continue as participant has been matched
                                continue;
                            }
                            // If participant has not been matched with Leader in previous slot
                            if (participant.matchedWith().stream().noneMatch(match -> match.leaderMatchedWith().email().equals(leaderEmail))) {
                                // Update Participant matchedWith
                                participant = updateParticipantMatchedWith(participant, leader, slot);
                                matchedParticipants.add(participant);

                                // Update Leader matchedWith
                                leader = updateLeaderMatchedWith(leader, participant, slot);
                                matchedLeaders.add(leader);

                                matches.add(new OneToOneMatch(leader, participant));

                                numberOfLeadersInBatch--;
                                numberOfParticipantsInBatch--;
                                break;
                            } else {
                                // If participant cannot match with any remaining leaders because they have a match
                                // in a previous slot
                                List<Leader> leadersNotAlreadyInMatched = new ArrayList<>(leadersWithPreference);
                                leadersNotAlreadyInMatched.removeAll(matchedLeaders);
                                int numberOfUnmatchedLeaders = leadersNotAlreadyInMatched.size();
                                int participantAlreadyMatchedWithNumberOfLeadersInThisList = 0;
                                for (ParticipantMatch match : participant.matchedWith()) {
                                    for (Leader unmatchedLeader : leadersNotAlreadyInMatched) {
                                        if (unmatchedLeader.email().equals(match.leaderMatchedWith().email())) {
                                            participantAlreadyMatchedWithNumberOfLeadersInThisList++;
                                        }
                                    }
                                }
                                if (participantAlreadyMatchedWithNumberOfLeadersInThisList == numberOfUnmatchedLeaders) {
                                    System.out.println("Participant " + participant.name() + " cannot be matched with any remaining leaders in this batch.");
                                    numberOfParticipantsInBatch--;
                                }
                            }
                        }
                        if (numberOfLeadersInBatch == 0 || numberOfParticipantsInBatch == 0) {
                            // Get remaining participants and add them to the unmatched
                            System.out.println("IN CONDITION - WHILE LOOP DID NOT BREAK");
                            List<Participant> unmatchedParticipantsInBatch = new ArrayList<>(participantsWithPreference);
                            unmatchedParticipantsInBatch.removeAll(matchedParticipants);
                            unmatchedParticipants.addAll(unmatchedParticipantsInBatch);

                            List<Leader> unmatchedLeadersInBatch = new ArrayList<>(leadersWithPreference);
                            unmatchedLeadersInBatch.removeAll(matchedLeaders);
                            unmatchedLeaders.addAll(unmatchedLeadersInBatch);

                            break;
                        }
//                        if (numberOfParticipantsInBatch == 0) {
//                            // Get remaining leaders and add them to the unmatched
//                            System.out.println("IN CONDITION - WHILE LOOP DID NOT BREAK");
//                            List<Leader> unmatched = new ArrayList<>(leadersWithPreference);
//                            unmatched.removeAll(matchedLeaders);
//                            unmatchedLeaders.addAll(unmatched);
//                            break;
//                        }
                    }
                    if (numberOfLeadersInBatch == 0 || numberOfParticipantsInBatch == 0) {
                        break;
                    }
                }

//                for (Leader leader : leadersWithPreference) {
//                    String leaderEmail = leader.email();
//                    if (matchedLeaders.stream().anyMatch(matchedLeader -> matchedLeader.email().equals(leaderEmail))) {
//                        // Continue as leader has been matched
//                        continue;
//                    }
//                    for (Participant participant : participantsWithPreference) {
//                        String participantEmail = participant.email();
//                        if (matchedParticipants.stream().anyMatch(matchedParticipant -> matchedParticipant.email().equals(participantEmail))) {
//                            // Continue as participant has been matched
//                            continue;
//                        }
//                        // If participant has not been matched with Leader in previous slot
//                        if (participant.matchedWith().stream().noneMatch(match -> match.leaderMatchedWith().email().equals(leaderEmail))) {
//                            // Update Participant matchedWith
//                            participant = updateParticipantMatchedWith(participant, leader, slot);
//                            matchedParticipants.add(participant);
//
//                            // Update Leader matchedWith
//                            leader = updateLeaderMatchedWith(leader, participant, slot);
//                            matchedLeaders.add(leader);
//
//                            matches.add(new OneToOneMatch(leader, participant));
//
//                            numberOfLeadersInBatch--;
//                            numberOfParticipantsInBatch--;
//                            break;
//                        }
//                    }
//
//                    if (numberOfLeadersInBatch == 0) {
//                        // Get remaining participants and add them to the unmatched
//                        List<Participant> unmatched = new ArrayList<>(participantsWithPreference);
//                        unmatched.removeAll(matchedParticipants);
//                        unmatchedParticipants.addAll(unmatched);
//                        break;
//                    }
//                    if (numberOfParticipantsInBatch == 0) {
//                        // Get remaining leaders and add them to the unmatched
//                        List<Leader> unmatched = new ArrayList<>(leadersWithPreference);
//                        unmatched.removeAll(matchedLeaders);
//                        unmatchedLeaders.addAll(unmatched);
//                        break;
//                    }
//                }

                System.out.println();
            }
            int unmatchedLeadersBatch = unmatchedLeaders.size();
            int unmatchedParticipantsBatch = unmatchedParticipants.size();

            for (Leader leader : unmatchedLeaders) {
                String leaderEmail = leader.email();
                if (matches.stream().anyMatch(match -> match.leader().email().equals(leaderEmail))) {
                    // Continue as leader has been matched
                    continue;
                }
                for (Participant participant : unmatchedParticipants) {
                    String participantEmail = participant.email();
                    if (matches.stream().anyMatch(match -> match.participant().email().equals(participantEmail))) {
                        // Continue as participant has been matched
                        continue;
                    }
                    if (participant.matchedWith().stream().noneMatch(match -> match.leaderMatchedWith().email().equals(leaderEmail))) {
                        // Update Participant matchedWith
                        participant = updateParticipantMatchedWith(participant, leader, slot);

                        // Update Leader matchedWith
                        leader = updateLeaderMatchedWith(leader, participant, slot);

                        matches.add(new OneToOneMatch(leader, participant));

                        unmatchedLeadersBatch--;
                        unmatchedParticipantsBatch--;
                        break;
                    }
                }
                if (unmatchedLeadersBatch == 0 && unmatchedParticipantsBatch == 0) {
                    break;
                }
            }
            System.out.println();
            finalMatches.put(slot, matches);
        }
        System.out.println();

        for (Map.Entry<String, List<OneToOneMatch>> slot : finalMatches.entrySet()) {
            List<OneToOneMatch> matches = slot.getValue();
            System.out.println("===========================================");
            System.out.println("SLOT: " + slot.getKey());
            for (OneToOneMatch match : matches){
                System.out.println("Leader:\t" + match.leader().name() + "\t" + match.leader().email() + "\t" + match.leader().preference()
                        + " has been matched with Participant:\t" + match.participant().name() + "\t" + match.participant().email() + "\t" + match.participant().preference());
            }
            System.out.println("===========================================");
        }

    }

    private Participant updateParticipantMatchedWith(Participant participant, Leader leader, String slot) {
        ArrayList<ParticipantMatch> participantMatchedWith = participant.matchedWith();
        participantMatchedWith.add(new ParticipantMatch(slot, new LeaderDto(leader.name(), leader.email(), leader.preference())));
        return participant.updateMatchedWith(participantMatchedWith);
    }

    private Leader updateLeaderMatchedWith(Leader leader, Participant participant, String slot) {
        ArrayList<ParticipantDto> participantList = new ArrayList<>();
        participantList.add(new ParticipantDto(participant.name(), participant.email(), participant.preference()));
        LeaderMatch leaderMatch = new LeaderMatch(slot, participantList);
        ArrayList<LeaderMatch> leaderMatchedWith = leader.matchedWith();
        leaderMatchedWith.add(leaderMatch);
        return leader.updateMatchedWith(leaderMatchedWith);
    }
}
