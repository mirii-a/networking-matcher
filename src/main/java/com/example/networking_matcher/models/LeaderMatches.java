package com.example.networking_matcher.models;

import java.util.List;

public record LeaderMatches(
        String slot,
        List<Participant> participantsMatchedWith
) {
}
