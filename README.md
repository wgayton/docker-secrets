# docker-secrets
A small library for loading [Docker Secrets](https://docs.docker.com/engine/swarm/secrets/)

[![Build Status](https://travis-ci.org/Hazz223/docker-secrets.svg?branch=master)](https://travis-ci.org/Hazz223/docker-secrets)
[![](https://jitpack.io/v/Hazz223/docker-secrets.svg)](https://jitpack.io/#Hazz223/docker-secrets)
[![Apache 2.0](https://img.shields.io/badge/license-apache--2.0-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0) 

## Importing and Using

### Importing
Currently the only way to import this library is via [jitpack](https://jitpack.io/#Hazz223/docker-secrets). This allows 
you grab and import the project as an artifact from Github. 

You'll need to add `jitpack` to your repository list in gradle.
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

and you'll need to add the dependency:

```groovy 
dependencies {
    compile 'com.github.Hazz223:docker-secrets:X.X.X'
}
```

### Using the Project

Once the project has been imported, call the `DockerSecretsLoaderBuilder.builder()`, which will return an instance of the builder.
For the defaults, use this line: `DockerSecretsLoaderBuilder.builder().build()`. This will expect secrets to be kept in the default
location - `/run/secrets/`

The Loader can also be customised:
- Calling `.withSecretFolder("my customer secret folder"")` will allow a defined secrets location
- Calling `withFileLoader(mySpecialFileLoader)` will enable the functionality to pass a custom implementation of `DockerSecretsFileLoader`,
 if the default one does not suffice.

After any/all of the above, `.build()` needs to be called to return an instance of `DockerSecretsLoader`.


### Methods
Two methods are available:
- `loadAsMap()`
- `loadAsProperties()`
Where the key is the secret file name, and the value is the secret files content.  

If the secrets folder directory is empty, a `DockerSecretsException` is thrown.
If any of the secret files can't be read, a `DockerSecretsException` is thrown.


### Use with Spring Boot
I've had a few requests on how to use the project with Spring Boot. Here's a current working example:

```Java
@Configuration
public class SecretsConfiguration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void loadScrets(){

        try{
            Map<String, String> dockerSecrets = DockerSecretsLoaderBuilder.build().loadAsMap();
            dockerSecrets.forEach(System::setProperty);
        } catch (DockerSecretsException ex){
            log.warn("Failed to load secrets", ex);
        }
    }
}
```

## Requirements 
There are no external requirements for the project, however it is currently Java 8 only. 
 
## Contributing
If you'd like to add more to this project, please fork the project and submit a pull request. You can also contact me 
on [twitter](https://twitter.com/Hazz223).

## Licence
This is under the Apache 2.0 license. More info can be found [here](https://github.com/Hazz223/docker-secrets/blob/master/LICENCE.md)
