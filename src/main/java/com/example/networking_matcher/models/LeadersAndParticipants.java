package com.example.networking_matcher.models;

import java.util.List;

public record LeadersAndParticipants(
        List<Leader> leaders,
        List<Participant> participants
) {
}
