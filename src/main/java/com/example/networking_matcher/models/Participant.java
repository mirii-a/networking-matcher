package com.example.networking_matcher.models;

import java.util.List;

public record Participant(
        String name,
        String email,
        String preference,
        List<ParticipantMatches> matchedWith
) {
    public Participant {}

    public Participant(String name, String email, String preference) {
        this(name, email, preference, List.of());
    }

    public Participant updateMatchedWith(List<ParticipantMatches> matchedWith) {
        return new Participant(name, email, preference, matchedWith);
    }
}
