package com.testesoftware.cadastro.vcr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class VCRService {

    private final ObjectMapper objectMapper;
    private static final String CASSETTE_DIR = "src/test/resources/vcr_cassettes";

    public VCRService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void saveCassette(String cassetteName, List<VCRInteraction> interactions) throws IOException {
        Path dirPath = Paths.get(CASSETTE_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String fileName = cassetteName + ".json";
        Path filePath = dirPath.resolve(fileName);

        VCRRecording cassette = new VCRRecording();
        cassette.setInteractions(interactions);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), cassette);
    }

    public VCRRecording loadCassette(String cassetteName) throws IOException {
        String fileName = cassetteName + ".json";
        Path filePath = Paths.get(CASSETTE_DIR).resolve(fileName);

        if (!Files.exists(filePath)) {
            return new VCRRecording();
        }

        return objectMapper.readValue(filePath.toFile(), VCRRecording.class);
    }

    public boolean cassetteExists(String cassetteName) {
        String fileName = cassetteName + ".json";
        Path filePath = Paths.get(CASSETTE_DIR).resolve(fileName);
        return Files.exists(filePath);
    }

    public List<VCRInteraction> getInteractionsForUrl(String cassetteName, String url, String method) throws IOException {
        VCRRecording recording = loadCassette(cassetteName);
        return recording.getInteractions().stream()
                .filter(interaction -> interaction.getUrl().equals(url) && interaction.getMethod().equals(method))
                .collect(Collectors.toList());
    }
}