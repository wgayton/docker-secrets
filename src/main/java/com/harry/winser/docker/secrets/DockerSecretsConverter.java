package com.harry.winser.docker.secrets;

import java.util.Set;

public interface DockerSecretsConverter<T> {

    T convert(Set<DockerSecret> dockerSecretSet) throws DockerSecretsException;

    T convert(DockerSecret dockerSecret) throws DockerSecretsException;

}
