package io.metro.docker.secrets.internal;

import io.metro.docker.secrets.DockerSecretsFileLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DockerSecretsFileLoaderTest {

    private DockerSecretsFileLoader dockerSecretsFileLoader;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() throws IOException {

        this.temporaryFolder.create();
        this.dockerSecretsFileLoader = new io.metro.docker.secrets.internal.DockerSecretsFileLoaderImpl();
    }

    @Test
    public void shouldLoadSecretsFromDirectory() throws IOException {

        Set<File> expected = this.givenTwoSecretFiles();

        Set<File> actual = this.dockerSecretsFileLoader.loadSecretsDirectory(this.temporaryFolder.getRoot().getAbsolutePath());

        assertEquals(actual, expected);
    }

    @Test
    public void shouldThrowExceptionWhenDirectoryNotFound() {

        String secretsDir = "/some/random/dir";
        this.expectedException.expect(io.metro.docker.secrets.DockerSecretsException.class);
        this.expectedException.expectMessage(String.format("Given secrets directory not found (%s). Will not load secrets", secretsDir));
        this.dockerSecretsFileLoader.loadSecretsDirectory(secretsDir);
    }

    @Test
    public void shouldThrowExceptionWhenNoSecretsFoundInDirectory() {

        this.expectedException.expect(io.metro.docker.secrets.DockerSecretsException.class);
        this.expectedException.expectMessage("No files found for given secrets directory: " + this.temporaryFolder.getRoot().getAbsolutePath());
        this.dockerSecretsFileLoader.loadSecretsDirectory(this.temporaryFolder.getRoot().getAbsolutePath());

    }

    private Set<File> givenTwoSecretFiles() throws IOException {

        File first = this.temporaryFolder.newFile("first-secrets");
        Files.write(first.toPath(), "first-secrets-code".getBytes());

        File second = this.temporaryFolder.newFile("second-secrets");
        Files.write(second.toPath(), "second-secrets-code".getBytes());

        Set<File> secretFiles = new HashSet<>();
        secretFiles.add(first);
        secretFiles.add(second);

        return secretFiles;
    }
}
