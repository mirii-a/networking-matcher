package com.example.networking_matcher.models;

import java.util.ArrayList;

public record Leader(
        String name,
        String email,
        String preference,
        ArrayList<LeaderMatch> matchedWith
) {
    public Leader {
    }

    public Leader(String name, String email, String preference) {
        this(name, email, preference, new ArrayList<>());
    }

    public Leader updateMatchedWith(ArrayList<LeaderMatch> matchedWith) {
        return new Leader(name, email, preference, matchedWith);
    }
}
