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

    /**
     * The path to the directory where the Docker Secrets are loaded.
     * @param rootFolder
     */
    public DockerSecretsLoader(String rootFolder) {
        this.secretsRootFolder = rootFolder;
    }

    /**
     * Default constructor. The Default location for Docker Secrets is defined by the Docker documentation.
     * The default it '/run/secrets/'
     */
    public DockerSecretsLoader() {
        this.secretsRootFolder = "/run/secrets/";
    }


    /**
     * Will load all secrets into a HashMap. If the Root folder isn't found, or there are no files in the Root folder, then
     * no secrets will be loaded, and a log message will be produced.
     * @return A map of the secrets found in a folder, where the key is the name of the file, and the value is the secret
     */
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