package com.example.networking_matcher.models;

public record ParticipantMatch(
        String slot,
        LeaderDto leaderMatchedWith
) {
}
