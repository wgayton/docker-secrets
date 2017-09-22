package io.metro.docker.secrets;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DockerSecretsLoaderTest {

    private static final String FIRST_SECRET_NAME = "first-secrets";
    private static final String FIRST_SECRET_VALUE = "first-secrets-code";
    private static final String SECOND_SECRET_NAME = "second-secrets";
    private static final String SECOND_SECRET_VALUE = "second-secrets-code";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DockerSecretsLoader dockerSecretsLoader;

    @Before
    public void init() throws IOException {

        this.temporaryFolder.create();
        this.dockerSecretsLoader = DockerSecretsLoaderBuilder.builder()
                .withSecretFolder(this.temporaryFolder.getRoot().getAbsolutePath())
                .build();
    }

    @Test
    public void shouldLoadSecretsAsMap() throws IOException {

        Map<String, String> expected = this.givenTwoSecrets();

        Map<String, String> actual = this.dockerSecretsLoader.loadAsMap();

        assertEquals(actual, expected);
    }

    @Test
    public void shouldLoadSecretsAsProperties() throws IOException {

        Map<String, String> expected = this.givenTwoSecrets();

        Properties actual = this.dockerSecretsLoader.loadAsProperties();

        assertThat(actual, equalTo(expected));
    }

    private Map<String, String> givenTwoSecrets() throws IOException {

        File first = this.temporaryFolder.newFile(FIRST_SECRET_NAME);
        Files.write(first.toPath(), FIRST_SECRET_VALUE.getBytes());

        File second = this.temporaryFolder.newFile(SECOND_SECRET_NAME);
        Files.write(second.toPath(), SECOND_SECRET_VALUE.getBytes());

        Map<String, String> secretsMap = new HashMap<>();
        secretsMap.put(FIRST_SECRET_NAME, FIRST_SECRET_VALUE);
        secretsMap.put(SECOND_SECRET_NAME, SECOND_SECRET_VALUE);
        return secretsMap;
    }
}
