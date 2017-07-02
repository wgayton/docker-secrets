package com.harry.winser.docker.secrets;


import java.io.File;
import java.util.Set;

public interface DockerSecretsFileLoader {

    Set<File> loadSecretsDirectory(String secretsDirLocation) throws DockerSecretsException;

}
