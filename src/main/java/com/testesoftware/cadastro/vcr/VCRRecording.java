package com.testesoftware.cadastro.vcr;

import java.util.ArrayList;
import java.util.List;

public class VCRRecording {
    private List<VCRInteraction> interactions = new ArrayList<>();

    public List<VCRInteraction> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<VCRInteraction> interactions) {
        this.interactions = interactions;
    }

    public void addInteraction(VCRInteraction interaction) {
        this.interactions.add(interaction);
    }
}