package com.harry.winser.docker.secrets;

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
     * @return A map of the secrets where the file name is the key, and the secret is the value
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secret
     */
    public Map<String, String> loadAsMap() throws DockerSecretsException {

        File secretsFolder = new File(secretsRootFolder);

        if (secretsFolder.exists()) {
            File[] secretFiles = secretsFolder.listFiles();

            if(secretFiles == null || secretFiles.length == 0){
                throw new DockerSecretsException("No files found for given Secrets folder: " + this.secretsRootFolder);
            }

            return this.getSecretsFromFiles(secretFiles);

        } else {
            String logMessage = String.format("Given secrets folder not found (%s). Will not load secrets", this.secretsRootFolder);
            throw new DockerSecretsException(logMessage);
        }
    }

    private Map<String, String> getSecretsFromFiles(File[] files) throws DockerSecretsException {

        Map<String, String> secretsMap = new HashMap<>();

        for (File file : files) {
            this.buildDockerSecret(file).ifPresent(secret -> secretsMap.put(secret.getName(), secret.getValue()));
        }

        return secretsMap;
    }

    private Optional<DockerSecret> buildDockerSecret(final File file) throws DockerSecretsException {

        Optional<DockerSecret> secretOpt;

        try {
            String secretBody = new String(Files.readAllBytes(file.toPath()));
            secretBody = secretBody.replace("\n", "").replaceAll("\r", "");

            secretOpt = Optional.of(new DockerSecret(file.getName(), secretBody));
        } catch (IOException ex) {

            String errorMessage = String.format("Failed to load Secret from file %s.", file.getName());
            throw new DockerSecretsException(errorMessage, ex);
        }

        return secretOpt;
    }
}