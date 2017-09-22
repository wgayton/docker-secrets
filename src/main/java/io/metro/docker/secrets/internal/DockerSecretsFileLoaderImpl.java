package io.metro.docker.secrets.internal;


import io.metro.docker.secrets.DockerSecretsException;
import io.metro.docker.secrets.DockerSecretsFileLoader;

import java.io.File;
import java.util.*;

public class DockerSecretsFileLoaderImpl implements DockerSecretsFileLoader {

    public Set<File> loadSecretsDirectory(String secretsDirLocation) throws DockerSecretsException {

        File secretsDir = new File(secretsDirLocation);

        if (!secretsDir.exists()) {
            String logMessage = String.format("Given secrets directory not found (%s). Will not load secrets", secretsDir);
            throw new DockerSecretsException(logMessage);
        }

        return this.getSecretFilesFromDir(secretsDir);
    }

    private Set<File> getSecretFilesFromDir(File secretsDir) throws DockerSecretsException {

        File[] secretFilesArray = Optional.ofNullable(secretsDir.listFiles()).orElse(new File[0]);

        if (secretFilesArray.length == 0) {
            throw new DockerSecretsException("No files found for given secrets directory: " + secretsDir);
        }

        Set<File> secretFiles = new HashSet<>();
        Collections.addAll(secretFiles, secretFilesArray);

        return secretFiles;
    }
}
