package com.example.networking_matcher.matching;

import com.example.networking_matcher.models.*;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.*;

@Service
public class MatchingOneToOneService {

    public MatchingOneToOneService() {
    }

    public HashMap<String, List<OneToOneMatch>> matchOneToOne(LeadersAndParticipants leadersAndParticipants) throws Exception {

        List<Leader> leaders = leadersAndParticipants.leaders();
        List<Participant> participants = leadersAndParticipants.participants();

        int numberOfSlots = 3;

        HashSet<String> preferences = getCompleteSetOfPreferencesFromEveryone(leaders, participants);

        HashMap<String, List<OneToOneMatch>> finalMatches = new HashMap<>();

        for (int i = 0; i < numberOfSlots; i++) {
            String slot = "slot" + (i + 1);
            List<OneToOneMatch> matches = new ArrayList<>();
            List<Leader> unmatchedLeaders = new ArrayList<>();
            List<Participant> unmatchedParticipants = new ArrayList<>();
            for (String preference : preferences) {
                List<Leader> leadersWithPreference = leaders.stream()
                        .filter(leader -> leader.preference().equals(preference)).toList();
                List<Participant> participantsWithPreference = participants.stream()
                        .filter(participant -> participant.preference().equals(preference)).toList();

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

                if (numberOfParticipantsInBatch != 0) {
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
                                    int participantAlreadyMatchedWithNumberOfLeadersInThisList =
                                            getNumberOfLeadersParticipantIsAlreadyMatchedWithFromUnmatchedLeaders(
                                                    participant, leadersNotAlreadyInMatched);
                                    if (participantAlreadyMatchedWithNumberOfLeadersInThisList == numberOfUnmatchedLeaders) {
                                        System.out.println("Participant " + participant.name() + " cannot be matched with any remaining leaders in this batch.");
                                        numberOfParticipantsInBatch--;
                                    }
                                }
                            }
                            if (numberOfLeadersInBatch == 0 || numberOfParticipantsInBatch == 0) {
                                // Get remaining participants and add them to the unmatched
                                List<Participant> unmatchedParticipantsInBatch = new ArrayList<>(participantsWithPreference);
                                unmatchedParticipantsInBatch.removeAll(matchedParticipants);
                                unmatchedParticipants.addAll(unmatchedParticipantsInBatch);

                                List<Leader> unmatchedLeadersInBatch = new ArrayList<>(leadersWithPreference);
                                unmatchedLeadersInBatch.removeAll(matchedLeaders);
                                unmatchedLeaders.addAll(unmatchedLeadersInBatch);

                                break;
                            }
                        }
                        if (numberOfLeadersInBatch == 0 || numberOfParticipantsInBatch == 0) {
                            break;
                        }
                    }
                }
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
            // Check if there are any leaders or participants that do not have a match from unmatched
            int expectedSize = i + 1;
            List<Leader> stillUnmatchedLeaders = unmatchedLeaders.stream().filter(leader -> leader.matchedWith().size() < expectedSize).toList();
            List<Participant> stillUnmatchedParticipants = unmatchedParticipants.stream().filter(participant -> participant.matchedWith().size() < expectedSize).toList();
            if (!stillUnmatchedParticipants.isEmpty() && !stillUnmatchedLeaders.isEmpty()) {
                List<OneToOneMatch> newMatches = new ArrayList<>();
                List<OneToOneMatch> matchesToRemove = new ArrayList<>();
                for (int index = 0; index < stillUnmatchedParticipants.size(); index ++) {
                    boolean isMatchedToAllRemainingLeaders = isParticipantMatchedWithAllOfTheGivenLeaders(stillUnmatchedParticipants.get(index), stillUnmatchedLeaders);
                    if (isMatchedToAllRemainingLeaders) {
                        // Loop through unmatchedLeaders and matched participants, if matched participant is not matched with stillUnmatchedLeader,
                        // swap the matches if the stillUnmatchedParticipant also is not already matched to them
                        for (OneToOneMatch match : matches) {
                            if (!isParticipantMatchedWithLeader(match.participant(), stillUnmatchedLeaders.get(index))) {
                                Leader leaderToSwap = match.leader();
                                if (!isParticipantMatchedWithLeader(stillUnmatchedParticipants.get(index), leaderToSwap)) {
                                    Participant updatedParticipant = giveInitialParticipantNewLeader(match.participant(), stillUnmatchedLeaders.get(index), slot);
                                    updateLeaderMatchedWith(stillUnmatchedLeaders.get(index), match.participant(), slot);
                                    newMatches.add(new OneToOneMatch(stillUnmatchedLeaders.get(index), updatedParticipant));

                                    updateLeaderMatchedWith(leaderToSwap, stillUnmatchedParticipants.get(index), slot);
                                    updateParticipantMatchedWith(stillUnmatchedParticipants.get(index), leaderToSwap, slot);
                                    newMatches.add(new OneToOneMatch(leaderToSwap, stillUnmatchedParticipants.get(index)));

                                    matchesToRemove.add(match);
                                    break;
                                }
                            }
                        }
                    } else {
                        throw new Exception("Serious algorithmic error has occurred. This program is not trustworthy.");
                    }
                }
                matches.removeAll(matchesToRemove);
                matches.addAll(newMatches);
            }
            finalMatches.put(slot, matches);
        }
//        excelService.createExcelNotebook(finalMatches);
        // TODO: Leaders have a strange number of participants assigned but everything else looks good
        return finalMatches;
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

    private Participant giveInitialParticipantNewLeader(Participant initialParticipant, Leader newLeader, String slot) {
        ArrayList<ParticipantMatch> participantInitiallyMatchedWith = initialParticipant.matchedWith();
        ArrayList<ParticipantMatch> newMatch = new ArrayList<>();
        ArrayList<ParticipantMatch> matchToRemove = new ArrayList<>();
        for (ParticipantMatch match : participantInitiallyMatchedWith) {
            if (match.slot().equals(slot)) {
                newMatch.add(new ParticipantMatch(slot, new LeaderDto(newLeader.name(), newLeader.email(), newLeader.preference())));
                matchToRemove.add(match);
            }
        }
        participantInitiallyMatchedWith.removeAll(matchToRemove);
        participantInitiallyMatchedWith.addAll(newMatch);

        return initialParticipant;
    }

    private int getNumberOfLeadersParticipantIsAlreadyMatchedWithFromUnmatchedLeaders(Participant participant, List<Leader> leadersNotAlreadyInMatched) {
        int participantAlreadyMatchedWithNumberOfLeadersInThisList = 0;
        for (ParticipantMatch match : participant.matchedWith()) {
            for (Leader unmatchedLeader : leadersNotAlreadyInMatched) {
                if (unmatchedLeader.email().equals(match.leaderMatchedWith().email())) {
                    participantAlreadyMatchedWithNumberOfLeadersInThisList++;
                }
            }
        }
        return participantAlreadyMatchedWithNumberOfLeadersInThisList;
    }

    private HashSet<String> getCompleteSetOfPreferencesFromEveryone(List<Leader> leaders, List<Participant> participants) {
        HashSet<String> preferences = new HashSet<>();

        leaders.forEach(leader -> preferences.add(leader.preference()));
        participants.forEach(participant -> preferences.add(participant.preference()));

        return preferences;
    }

    private boolean isParticipantMatchedWithAllOfTheGivenLeaders(Participant participant, List<Leader> leaders) {
        for (Leader leader : leaders) {
            String leaderEmail = leader.email();
            if (participant.matchedWith().stream().noneMatch(match -> match.leaderMatchedWith().email().equals(leaderEmail))) {
                return false;
            }
        }
        return true;
    }

    private boolean isParticipantMatchedWithLeader(Participant participant, Leader leader) {
        return participant.matchedWith().stream().anyMatch(match -> match.leaderMatchedWith().email().equals(leader.email()));
    }
}
