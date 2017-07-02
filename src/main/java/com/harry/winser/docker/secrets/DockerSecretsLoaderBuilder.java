package com.harry.winser.docker.secrets;


import com.harry.winser.docker.secrets.internal.DockerSecretsFileLoaderImpl;
import com.harry.winser.docker.secrets.internal.DockerSecretsLoaderImpl;
import com.harry.winser.docker.secrets.internal.FileToDockerSecretConverter;

public class DockerSecretsLoaderBuilder {

    private static final String DEFAULT_SECRETS_LOCATION = "/run/secrets/";

    private DockerSecretsFileLoader fileLoader;
    private String secretsDir;

    public static DockerSecretsLoaderBuilder builder(){
        return new DockerSecretsLoaderBuilder();
    }

    public DockerSecretsLoaderBuilder withFileLoader(DockerSecretsFileLoader fileLoader) {
        this.fileLoader = fileLoader;
        return this;
    }

    public DockerSecretsLoaderBuilder withSecretFolder(String secretFolder) {
        this.secretsDir = secretFolder;
        return this;
    }

    public DockerSecretsLoader build() {

        if(this.fileLoader == null) {
            this.fileLoader = new DockerSecretsFileLoaderImpl();
        }

        if(this.secretsDir == null){
            this.secretsDir = DEFAULT_SECRETS_LOCATION;
        }

        return new DockerSecretsLoaderImpl(this.fileLoader,
                new FileToDockerSecretConverter(),
                this.secretsDir);
    }
}
