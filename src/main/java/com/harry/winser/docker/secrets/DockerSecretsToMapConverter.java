package com.harry.winser.docker.secrets;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DockerSecretsToMapConverter implements DockerSecretsConverter<Map<String, String>>{

    @Override
    public Map<String, String> convert(Set<DockerSecret> dockerSecretSet) {

        return dockerSecretSet.stream()
                .collect(Collectors.toMap(DockerSecret::getName, DockerSecret::getValue));
    }

    @Override
    public Map<String, String> convert(DockerSecret dockerSecret) {

        Map<String, String> dockerSecretsMap = new HashMap<>();
        dockerSecretsMap.put(dockerSecret.getName(), dockerSecret.getValue());
        return dockerSecretsMap;
    }
}
