package io.metro.docker.secrets.internal;

import io.metro.docker.secrets.DockerSecretsException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class FileToDockerSecretConverterTest {

    private FileToDockerSecretConverter converter = new FileToDockerSecretConverter();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() throws IOException {
        this.temporaryFolder.create();
    }

    @Test
    public void shouldCreateDockerSecretFromFile() throws IOException {

        File secretFile = this.givenSecretFile();

        io.metro.docker.secrets.internal.DockerSecret actual = this.converter.convert(secretFile);
        assertEquals(actual.getName(), "first-secrets");
        assertEquals(actual.getValue(),"first-secrets-code");
    }

    @Test
    public void shouldThrowExceptionWhenFileIsUnreadable() throws IOException {

        File unreadableFile = this.givenUnreadableFile();

        this.expectedException.expect(DockerSecretsException.class);
        this.expectedException.expectMessage(String.format("Failed to load Secret from file %s.", unreadableFile.getName()));
        this.converter.convert(unreadableFile);
    }

    private File givenUnreadableFile() throws IOException {
        File file = this.givenSecretFile();
        file.setReadable(false);

        return file;
    }

    private File givenSecretFile() throws IOException {

        File first = this.temporaryFolder.newFile("first-secrets");
        Files.write(first.toPath(), "first-secrets-code".getBytes());

        return first;
    }
}
