package com.example.networking_matcher.models;

import java.util.List;

public record LeaderMatch(
        String slot,
        List<ParticipantDto> participantsMatchedWith
) {
}
