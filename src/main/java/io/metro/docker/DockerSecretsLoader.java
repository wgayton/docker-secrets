package io.metro.docker;

import io.metro.docker.secrets.DockerSecretsException;
import io.metro.docker.secrets.DockerSecretsLoaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DockerSecretsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(DockerSecretsLoader.class);

    public static String replace(String key) {
        String[] profiles = {"docker_", "local_", "stage_", "production_", "prod_", "development_", "dev_"};

        for(String profile : profiles) {
            key = key.replaceFirst(profile, "").replaceFirst(profile.toUpperCase(), "");
        }

        return key;
    }

    public static Map<String, String> loadSecrets() {
        String directory = "/run/secrets/";

        return loadSecrets(directory);
    }

    public static Map<String, String> loadSecrets(String directory) {
        File secretsDirectory = new File(directory);
        boolean secretsDirectoryExists = secretsDirectory.exists();

        if(!secretsDirectoryExists) {
            try {
                secretsDirectory = Paths.get(DockerSecretsLoader.class.getResource(directory).toURI()).toFile();
                secretsDirectoryExists = secretsDirectory.exists();
            } catch (Exception ex) {
                LOG.info("Failed to load secrets resource", ex.getCause());
            }
        }

        Map<String, String> secrets = new HashMap<>();
        if(secretsDirectoryExists) {
            LOG.info("Fetching secrets from: " + secretsDirectory.toString());
            try {
                Map<String, String> _secrets = DockerSecretsLoaderBuilder
                        .builder()
                        .withSecretFolder(secretsDirectory.getAbsolutePath())
                        .build()
                        .loadAsMap();

                for (Map.Entry<String, String> entry : _secrets.entrySet()) {
                    System.setProperty(replace(entry.getKey()), entry.getValue());
                    secrets.put(replace(entry.getKey()), entry.getValue());
                }
            } catch (DockerSecretsException ex) {
                LOG.warn("Could not load secrets:", ex);
            }
        }

        return secrets;
    }
}
