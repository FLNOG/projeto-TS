package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VCRRecordingTest {

    @Test
    void testInitialInteractionsListIsEmpty() {
        VCRRecording recording = new VCRRecording();

        assertNotNull(recording.getInteractions());
        assertTrue(recording.getInteractions().isEmpty());
    }

    @Test
    void testSetInteractions() {
        VCRRecording recording = new VCRRecording();
        List<VCRInteraction> list = new ArrayList<>();
        list.add(new VCRInteraction());

        recording.setInteractions(list);

        assertEquals(1, recording.getInteractions().size());
    }

    @Test
    void testAddInteraction() {
        VCRRecording recording = new VCRRecording();
        VCRInteraction interaction = new VCRInteraction();

        recording.addInteraction(interaction);

        assertEquals(1, recording.getInteractions().size());
        assertEquals(interaction, recording.getInteractions().get(0));
    }
}