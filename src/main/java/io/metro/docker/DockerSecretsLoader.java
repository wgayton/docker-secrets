package io.metro.docker;

import io.metro.docker.secrets.DockerSecretsException;
import io.metro.docker.secrets.DockerSecretsLoaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DockerSecretsLoader {
    private static Logger LOG = LoggerFactory.getLogger(DockerSecretsLoader.class);

    public static String replace(String key) {
        String nKey = key;
        String regex;
        String[] profiles = {"docker_", "local_", "stage_", "production_"};

        for(String profile : profiles) {
            nKey = nKey.replaceFirst(profile, "").replaceFirst(profile.toUpperCase(), "");
        }

        return nKey;
    }

    public static Map<String, String> loadSecrets() {
        String directory = "/run/secrets/";
        File secretsDirectory = new File(directory);
        boolean secretsDirectoryExists = secretsDirectory.exists();

        if(!secretsDirectoryExists) {
            try {
                secretsDirectory = Paths.get(DockerSecretsLoader.class.getResource("/run/secrets/").toURI()).toFile();
                secretsDirectoryExists = secretsDirectory.exists();
            } catch (URISyntaxException | NullPointerException ex) {
                LOG.debug("Failed to load secrets resource", ex.getCause());
            }
        }

        Map<String, String> secrets = new HashMap<>();
        if(secretsDirectoryExists) {
            LOG.debug("Fetching secrets from: " + secretsDirectory.toString());
            try {
                Map<String, String> _secrets = DockerSecretsLoaderBuilder
                        .builder()
                        .withSecretFolder(secretsDirectory.getAbsolutePath())
                        .build()
                        .loadAsMap();

                for (Map.Entry<String, String> entry : _secrets.entrySet()) {
                    secrets.put(replace(entry.getKey()), entry.getValue());
                    LOG.debug("Setting secret: {} = {}", replace(entry.getKey()), entry.getValue());
                }

                setEnv(secrets);
            } catch (DockerSecretsException ex) {
                LOG.warn("Could not load secrets:", ex);
            }
        }

        return secrets;
    }

    private static void setEnv(Map<String, String> newEnv) {
        try
        {
            Class<?> envClass = Class.forName("java.lang.ProcessEnvironment");
            Field caseSensitiveEnvironment = envClass.getDeclaredField("caseSensitiveEnvironment");
            caseSensitiveEnvironment.setAccessible(true);
            ((Map<String, String>) caseSensitiveEnvironment.get(null)).putAll(newEnv);

            Field caseInsensitiveEnvironmentField = envClass.getDeclaredField("caseInsensitiveEnvironmentField");
            caseInsensitiveEnvironmentField.setAccessible(true);
            ((Map<String, String>) caseInsensitiveEnvironmentField.get(null)).putAll(newEnv);
        }
        catch (NoSuchFieldException e)
        {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for(Class cl : classes) {
                    if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newEnv);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
