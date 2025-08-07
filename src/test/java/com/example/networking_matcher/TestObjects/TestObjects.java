package com.example.networking_matcher.TestObjects;

import com.example.networking_matcher.models.Leader;
import com.example.networking_matcher.models.Participant;

import java.util.ArrayList;
import java.util.List;

public class TestObjects {

    private Leader getLeader(String name, String email, String preference) {
        return new Leader(name, email, preference, new ArrayList<>());
    }

    private Participant getParticipant(String name, String email, String preference) {
        return new Participant(name, email, preference, new ArrayList<>());
    }

    public List<Leader> getListOfLeaders() {
        List<Leader> leaders = new ArrayList<>();
        leaders.add(avaMontgomery());
        leaders.add(marcusFlynn());
        leaders.add(taliaRivers());
        leaders.add(eliotTran());
        leaders.add(ianMcAllister());

        return leaders;
    }

    public List<Participant> getListOfParticipants() {
        List<Participant> participants = new ArrayList<>();
        participants.add(xavierChen());
        participants.add(islaKerrigan());
        participants.add(damonGreaves());
        participants.add(harperLin());
        participants.add(nikoArvidsson());

        return participants;
    }

    public Leader avaMontgomery() {
        return getLeader("Ava Montgomery", "ava.monty@creovault.com", "Finance");
    }

    public Leader marcusFlynn() {
        return getLeader("Marcus Flynn", "marc.flynn@inkspire.net", "Compliance");
    }

    public Leader taliaRivers() {
        return getLeader("Talia Rivers", "talia.r@draftcanvas.com", "HR");
    }

    public Leader eliotTran() {
        return getLeader("Eliot Tran", "eli.tran@designsyndicate.org", "Finance");
    }

    public Leader ianMcAllister() {
        return getLeader("Ian McAllister", "ian.m@brandplot.net", "Compliance");
    }

    public Participant xavierChen() {
        return getParticipant("Xavier Chen", "x.chen@metasync.tech", "Compliance");
    }

    public Participant islaKerrigan() {
        return getParticipant("Isla Kerrigan", "isla.k@fusionware.io", "Finance");
    }

    public Participant damonGreaves() {
        return getParticipant("Damon Greaves", "d.greaves@byteforge.tech", "Compliance");
    }

    public Participant harperLin() {
        return getParticipant("Harper Lin", "harper.lin@techlift.net", "Finance");
    }

    public Participant nikoArvidsson() {
        return getParticipant("Niko Arvidsson", "niko.a@quantarc.net", "Technology");
    }
}
