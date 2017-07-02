package com.harry.winser.docker.secrets.internal;

import com.harry.winser.docker.secrets.DockerSecretsException;
import com.harry.winser.docker.secrets.DockerSecretsFileLoader;
import com.harry.winser.docker.secrets.DockerSecretsLoader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DockerSecretsLoaderImpl implements DockerSecretsLoader {

    private final DockerSecretsFileLoader fileLoader;
    private final FileToDockerSecretConverter fileToDockerSecretConverter;
    private final String secretsRootFolder;

    public DockerSecretsLoaderImpl(DockerSecretsFileLoader fileLoader,
                                   FileToDockerSecretConverter fileToDockerSecretConverter,
                                   String secretsRootFolder) {

        this.fileLoader = fileLoader;
        this.fileToDockerSecretConverter = fileToDockerSecretConverter;
        this.secretsRootFolder = secretsRootFolder;
    }

    /**
     * Will load all secrets into a HashMap. If the Root folder isn't found, or there are no files in the Root folder, then
     * no secrets will be loaded, and a log message will be produced.
     *
     * @return A map of the secrets where the file name is the key, and the secrets is the value
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secrets
     */
    public Map<String, String> loadAsMap() throws DockerSecretsException {

        return this.getSecrets()
                .collect(Collectors.toMap(DockerSecret::getName, DockerSecret::getValue));
    }

    public Properties loadAsProperties() throws DockerSecretsException {

        Properties prop = new Properties();

        this.getSecrets()
                .forEach(secret -> prop.put(secret.getName(), secret.getValue()));

        return prop;
    }

    private Stream<DockerSecret> getSecrets() {

        return this.fileLoader.loadSecretsDirectory(this.secretsRootFolder).stream()
                .map(this.fileToDockerSecretConverter::convert);
    }
}