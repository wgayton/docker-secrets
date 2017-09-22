package io.metro.docker.secrets.internal;

import io.metro.docker.secrets.DockerSecretsException;
import io.metro.docker.secrets.DockerSecretsFileLoader;
import io.metro.docker.secrets.DockerSecretsLoader;

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