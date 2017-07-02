package com.harry.winser.docker.secrets;


import java.util.Map;
import java.util.Properties;

public interface DockerSecretsLoader {

    Map<String, String> loadAsMap();

    Properties loadAsProperties();

}
