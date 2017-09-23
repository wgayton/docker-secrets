package io.metro.docker;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DockerSecretsLoaderTest {
    @Test
    public void loadSecrets() throws URISyntaxException, IOException {
        Map<String, String> secrets = DockerSecretsLoader.loadSecrets();
        assertThat("MYSQL_ROOT_PASSWORD", is(secrets.get("MYSQL_ROOT_PASSWORD")));
        assertThat("MYSQL_APP_PASSWORD", is(secrets.get("MYSQL_APP_PASSWORD")));
        assertThat("RABBIT_SERVICE_PASS", is(secrets.get("RABBIT_SERVICE_PASS")));
        assertThat("GOOGLE_API_TOKEN", is(secrets.get("GOOGLE_API_TOKEN")));
        assertThat("SERVICE_PASSWORD", is(secrets.get("SERVICE_PASSWORD")));
    }
}
