package com.harry.winser.docker.secrets.internal;

import com.harry.winser.docker.secrets.DockerSecretsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileToDockerSecretConverter {

    DockerSecret convert(final File file) {

        try {

            String secretBody = new String(Files.readAllBytes(file.toPath()));
            secretBody = secretBody.replace("\n", "").replaceAll("\r", "");

            return new DockerSecret(file.getName(), secretBody);

        } catch (IOException ex) {

            String errorMessage = String.format("Failed to load Secret from file %s.", file.getName());
            throw new DockerSecretsException(errorMessage, ex);
        }
    }
}
