package com.harry.winser.docker.secrets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DockerSecretsLoader {
    private final String secretsRootFolder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DockerSecretsLoader(String rootFolder) {
        this.secretsRootFolder = rootFolder;
    }

    public DockerSecretsLoader() {
        this.secretsRootFolder = "/run/secrets/";
    }

    public Map<String, String> loadAsMap() {

        Map<String, String> secretsMap = new HashMap<>();

        File secretsFolder = new File(secretsRootFolder);

        if (secretsFolder.exists()) {
            File[] secretFiles = secretsFolder.listFiles();

            if (secretFiles != null) {

                secretsMap.putAll(Arrays.stream(secretFiles)
                        .map(this::buildDockerSecret)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toMap(DockerSecret::getName, DockerSecret::getValue)));
            } else {
                this.logger.warn("No files found for given Secrets folder: " + this.secretsRootFolder);
            }

        } else {
            String logMessage = String.format("Given secrets folder not found (%s). Will not load secrets", this.secretsRootFolder);
            this.logger.warn(logMessage);
        }

        return secretsMap;
    }

    private Optional<DockerSecret> buildDockerSecret(final File file) {

        Optional<DockerSecret> secretOpt = Optional.empty();

        try {
            String secretBody = new String(Files.readAllBytes(file.toPath()));
            secretBody = secretBody.replace("\n", "").replaceAll("\r", "");

            secretOpt = Optional.of(new DockerSecret(file.getName(), secretBody));
        } catch (IOException e) {
            this.logger.warn("Failed to load Secret for file " + file.getName() + ". Will be skipped");
        }
        return secretOpt;
    }
}