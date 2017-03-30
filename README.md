# docker-secrets
A small library for loading [Docker Secrets](https://docs.docker.com/engine/swarm/secrets/)

[![Build Status](https://travis-ci.org/Hazz223/docker-secrets.svg?branch=master)](https://travis-ci.org/Hazz223/docker-secrets)

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

Once you've imported the project, you can create an instance of `DockerSecretsLoader`. Unless a new path is given to the
Constructor, it will default to the standard location for Docker Secrets. 

Calling `loadAsMap()` will load all of the files in the defined secrets location, and then read their content. It will create
a map where there Key is the file name, and the Value is the the secrets value.

If it fails to find the root folder, or there are no secrets in this folder, or a secret can't be read, then a 
`DockerSecretsException` is thrown. 

## Requirements 
There are no external dependencies for this project. And i plan to keep it that way!

## About
This project arose because i needed an easy to way to get Docker Secrets loaded into a Springboot project.
However, i extracted all of the Spring stuff out of it, and moved it into a small library for everyone to enjoy!
 
## Contributing
If you'd like to add more to this project, please fork the project and submit a pull request. You can also contact me 
on [twitter](https://twitter.com/Hazz223).
