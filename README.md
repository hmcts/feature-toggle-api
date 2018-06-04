# Feature Toggle API

[![Build Status](https://travis-ci.org/hmcts/feature-toggle-api.svg?branch=master)](https://travis-ci.org/hmcts/feature-toggle-api)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ace85050970d4ea99e8034a4dc768b63)](https://www.codacy.com/app/HMCTS/feature-toggle-api)
[![codecov](https://codecov.io/gh/hmcts/feature-toggle-api/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/feature-toggle-api)

![LLD](/doc/arch/diagram.png)

![sequence](/doc/arch/sequence.png)

## Configuring Users for feature toggle

# Users and Roles
- There are three different types of users which can be configured for feature toggle

  - Admin: These users can login into FF4J web console and perform all operations on feature toggles(create, update, delete, enable, disable and access auditing information using web console).
  - Editor: These users cannot access the FF4J web console but can perform operations such as create, update, delete, enable and disable feature toggle through FF4J REST API.
  - Read: These users can only read feature toggle values through FF4J REST API and cannot perform any other operations. They also cannot access web console.
  
 :bulb: *Currently feature toggle values can be read without authentication.*
 
 - Roles are dynamically assigned based on the configuration.
 
## Configuring users using spring config file.
- Users needs to added to below section in [application.yaml](src/main/resources/application.yaml)
- Depending on the section in which you add users they will be assigned roles.
- Username and password are set as environment variable through terraform configuration.
 
```
webconsole:
  users:
    admins:
      - username: ${SERVICENAME_ADMIN_USERNAME}
        password: ${CMC_ADMIN_PASSWORD}
    editors:
      - username: ${SERVICENAME_EDITOR_USERNAME}
        password: ${CMC_EDITOR_PASSWORD}
    readers:
      - username: ${SERVICENAME_READ_USERNAME}
        password: ${SERVICENAME_READ_PASSWORD}
```

### Steps to setup environment variable through terraform are as below

 - Create secrets in Hashicorp vault at a path similar to `"secret/${var.vault_section}/cc/ff4j/webconsole/servicename-admin-user"` for both username and passwords.
 Passwords needs to be in plain text in the vault and they will be hashed(BCrypt Password encryption) before saving it in database.
 
 :bulb: *Recommendation is to configure username using pattern servicename-admin@hmcts.net and a strong secured password.
 Usernames have to be unique hence do not use generic user names.*
 
 - Add below code in [main.tf](infrastructure/main.tf)
  
    ```
    data "vault_generic_secret" "servicename-admin-user" {
       path = "secret/${var.vault_section}/cc/ff4j/webconsole/servicename-admin-user"
    }
    ```
    
     ```
     data "vault_generic_secret" "servicename-editor-password" {
       path = "secret/${var.vault_section}/cc/ff4j/webconsole/servicename-admin-password"
     }
    ```
    
    ```
     locals {
        servicename_admin_user   = "${data.vault_generic_secret.servicename-admin-user.data["value"]}"
        servicename_admin_password   = "${data.vault_generic_secret.servicename-admin-password.data["value"]}"
      }
    ```
    
    ```
      app_settings = {
       SERVICENAME_ADMIN_USERNAME          = "${local.servicename_admin_user}"
       SERVICENAME_ADMIN_PASSWORD          = "${local.servicename_admin_password}"
      }  
     ```

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew assemble
```

Run the distribution (created in `build/bootScripts/feature-toggle-api` directory)
by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port
(set to `8580` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:8580/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

### Alternative script to run application

To skip all the setting up and building, just execute the following command:

```bash
./bin/run-in-docker.sh
```

For more information:

```bash
./bin/run-in-docker.sh -h
```

Script includes bare minimum environment variables necessary to start api instance. Whenever any variable is changed or any other script regarding docker image/container build, the suggested way to ensure all is cleaned up properly is by this command:

```bash
docker-compose rm
```

It clears stopped containers correctly. Might consider removing clutter of images too, especially the ones fiddled with:

```bash
docker images

docker image rm <image-id>
```

There is no need to remove postgres and java or similar core images.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
