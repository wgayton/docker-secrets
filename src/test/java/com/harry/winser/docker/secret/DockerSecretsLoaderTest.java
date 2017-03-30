package com.harry.winser.docker.secret;

import com.harry.winser.docker.secrets.DockerSecretsLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerSecretsLoaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private DockerSecretsLoader dockerSecretsLoader;

    @Before
    public void init() throws IOException {

        this.tempFolder.create();
        this.dockerSecretsLoader = new DockerSecretsLoader(this.tempFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void shouldLoadSecretsFromFolder() throws IOException {

        Map<String, String> expectedSecrets = this.givenTwoSecrets();

        Map<String, String> actualSecrets = this.dockerSecretsLoader.loadAsMap();

        assertThat(actualSecrets).containsAllEntriesOf(expectedSecrets);
    }

    @Test
    public void shouldLoadSecretsWhenFilesAreEmptyEmpty() throws IOException {

        Map<String, String> expectedSecrets = this.givenTwoEmptySecretFiles();

        Map<String, String> actualSecrets = this.dockerSecretsLoader.loadAsMap();

        assertThat(actualSecrets).containsAllEntriesOf(expectedSecrets);
    }

    @Test
    public void shouldReturnEmptySetWhenNoSecretFilesFound() {

        Map<String, String> actualSecrets = this.dockerSecretsLoader.loadAsMap();
        assertThat(actualSecrets).isEmpty();
    }

    @Test
    public void shouldReturnEmptyWhenSecretsFolderNotFound() {

        this.dockerSecretsLoader = new DockerSecretsLoader("does not exist");

        Map<String, String> actualSecrets = this.dockerSecretsLoader.loadAsMap();
        assertThat(actualSecrets).isEmpty();
    }

    private Map<String, String> givenTwoSecrets() throws IOException {

        Map<String, String> expectedMap = new HashMap<>();

        String firstSecretFileName = "First Secret";
        String firstSecret = "Not a big fan of Ice cream";
        File firstSecretFile = this.tempFolder.newFile(firstSecretFileName);
        Files.write(firstSecretFile.toPath(), Collections.singleton(firstSecret));
        expectedMap.put(firstSecretFileName, firstSecret);

        String secondSecretFileName = "Second Secret";
        String secondSecret = "I really don't like the dark";
        File secondSecretFile = this.tempFolder.newFile(secondSecretFileName);
        Files.write(secondSecretFile.toPath(), Collections.singleton(secondSecret));
        expectedMap.put(secondSecretFileName, secondSecret);

        return expectedMap;
    }

    private Map<String, String> givenTwoEmptySecretFiles() throws IOException {

        Map<String, String> expectedMap = new HashMap<>();

        String firstSecretFileName = "First Secret";
        this.tempFolder.newFile(firstSecretFileName);
        expectedMap.put(firstSecretFileName, "");

        String secondSecretFileName = "Second Secret";
        this.tempFolder.newFile(secondSecretFileName);
        expectedMap.put(secondSecretFileName, "");

        return expectedMap;
    }
}
