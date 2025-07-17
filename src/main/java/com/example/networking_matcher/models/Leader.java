package com.example.networking_matcher.models;

import java.util.List;

public record Leader(
        String name,
        String email,
        String preference,
        List<LeaderMatches> matchedWith
) {
    public Leader {}

    public Leader(String name, String email, String preference) {
        this(name, email, preference, List.of());
    }

    public Leader updateMatchedWith(List<LeaderMatches> matchedWith) {
        return new Leader(name, email, preference, matchedWith);
    }
}
