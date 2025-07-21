package com.example.networking_matcher.models;

import java.util.ArrayList;
import java.util.List;

public record Participant(
        String name,
        String email,
        String preference,
        ArrayList<ParticipantMatch> matchedWith
) {
    public Participant {}

    public Participant(String name, String email, String preference) {
        this(name, email, preference, new ArrayList<>());
    }

    public Participant updateMatchedWith(ArrayList<ParticipantMatch> matchedWith) {
        return new Participant(name, email, preference, matchedWith);
    }
}
