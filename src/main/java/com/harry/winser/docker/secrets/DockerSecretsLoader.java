package com.harry.winser.docker.secrets;


import java.util.Map;
import java.util.Properties;

public interface DockerSecretsLoader {

    /**
     * Will load all secrets into a HashMap,with the key being the name of the secrets file, and the value being the
     * files content. These are not cached between subsequent calls. Each time they will be reloaded.
     * @return A map of the secrets where the file name is the key, and the secrets is the value
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secrets
     */
    Map<String, String> loadAsMap();

    /**
     * Will load all secrets into a Properties Object, with the key being the name of the secrets file, and the value being the
     * files content. These are not cached between subsequent calls. Each time they will be reloaded.
     * @return A map of the secrets where the file name is the key, and the secrets is the value
     * @throws DockerSecretsException when the root folder can't be found, or when no files found, or when fails to read a secrets
     */
    Properties loadAsProperties();

}
