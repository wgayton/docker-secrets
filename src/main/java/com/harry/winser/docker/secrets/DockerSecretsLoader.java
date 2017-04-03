package com.harry.winser.docker.secrets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class DockerSecretsLoader {

    private static final String DEFAULT_SECRETS_DIR = "/run/secrets/";
    private final String secretsRootFolder;

    /**
     * The path to the directory where the Docker Secrets are loaded.
     *
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
        this.secretsRootFolder = DEFAULT_SECRETS_DIR;
    }

    /**
     * Will load all secrets into a HashMap. If the Root folder isn't found, or there are no files in the Root folder, then
     * no secrets will be loaded, and a log message will be produced.
     *
     * @return A map of the secrets where the file name is the key, and the secret is the value
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secret
     */
    public Map<String, String> loadAsMap() throws DockerSecretsException {

        return (Map<String, String>) this.loadAs(new DockerSecretsToMapConverter());
    }

    /**
     * Will load all of the secrets into a Set of DockerSecret objects.
     * @return a Set of DockerSecret objects
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secret
     */
    public Set<DockerSecret> loadAsDockerSecrets() throws DockerSecretsException {

        Set<File> secretFiles = this.getDockerSecretsFile();

        return this.getSecretsFromFiles(secretFiles);
    }

    /**
     * Will load the secrets, and then convert them to an object of type T (defined by the user), and then returned as a Set of type T
     * @param converter The converter to convert from a DockerSecret to T
     * @param <T> The object of the return type
     * @return a Set of type T
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secret. The extended
     * Converter should also through a DockerSecretsException when the conversion fails.
     */
    public <T> Set<T> loadAsSetOf(DockerSecretsConverter<T> converter) throws DockerSecretsException {

        Set<DockerSecret> secrets = this.loadAsDockerSecrets();


        Set<T> resultSet = new HashSet<>();
        for (DockerSecret secret : secrets) {
            resultSet.add(converter.convert(secret));
        }


        return resultSet;
    }

    /**
     * Will load secrets as the Type given, T.
     * @param converter The converter to convert from a Set of Docker Secrets to the Type T
     * @param <T> The type to convert the Secrets to
     * @return The returned secrets, represented as Type T
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secret. The extended
     * Converter should also through a DockerSecretsException when the conversion fails.
     */
    public <T> Object loadAs(DockerSecretsConverter<T> converter) throws DockerSecretsException {

        Set<DockerSecret> secrets = this.loadAsDockerSecrets();

        return converter.convert(secrets);
    }

    private Set<DockerSecret> getSecretsFromFiles(Set<File> files) throws DockerSecretsException {

        Set<DockerSecret> secrets = new HashSet<>();
        for (File secretFile : files) {
            this.buildDockerSecret(secretFile).ifPresent(secrets::add);
        }

        return secrets;
    }

    private Set<File> getDockerSecretsFile() throws DockerSecretsException {

        File secretsFolder = new File(secretsRootFolder);

        if (secretsFolder.exists()) {
            File[] secretFiles = secretsFolder.listFiles();

            if (secretFiles == null || secretFiles.length == 0) {
                throw new DockerSecretsException("No files found for given Secrets folder: " + this.secretsRootFolder);
            }

            return new HashSet<>(Arrays.asList(secretsFolder.listFiles()));

        } else {
            String logMessage = String.format("Given secrets folder not found (%s). Will not load secrets", this.secretsRootFolder);
            throw new DockerSecretsException(logMessage);
        }
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