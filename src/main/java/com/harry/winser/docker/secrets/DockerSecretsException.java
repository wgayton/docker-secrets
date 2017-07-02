package com.harry.winser.docker.secrets;


public class DockerSecretsException extends RuntimeException {

    public DockerSecretsException(String message) {
        super(message);
    }

    public DockerSecretsException(String message, Throwable cause) {
        super(message, cause);
    }
}
