# ![SHOGun Logo](./docs/assets/img/shogun_logo_thumb.png) SHOGun

![](https://img.shields.io/github/license/terrestris/shogun)
![publish](https://github.com/terrestris/shogun/actions/workflows/build-and-publish.yml/badge.svg?branch=main)
![test](https://github.com/terrestris/shogun/actions/workflows/test.yml/badge.svg?branch=main)
<!-- ![dependabot](https://api.dependabot.com/badges/status?host=github&repo=terrestris/shogun) -->

SHOGun is an application framework written in Java for building scaleable web application backends in the context of
geospatial data infrastructures. It can be used directly without any specific customizations or highly customized to
meet the demands of flexible project requirements.

## In a nutshell

SHOGun…

* …is written in Java and supports Java >= 21.
* …is based on top of Spring / Spring Boot.
* …provides a set of configuration entities to manage the contents of a geospatial data infrastructure (e.g. layers,
applications, users or groups).
* …provides (secured) web interfaces (REST & GraphQL) for accessing and manipulating these entities (e.g. for creating
an application configuration).
* …separates its functionalities into isolated microservices (e.g. for proxying OGC requests) and is highly scalable.

## Development

### Requirements

For the development of SHOGun the following tools are required locally:

- maven >= 3.9
- Java 21
- docker and docker compose
- IntelliJ (recommended)
- [IntelliJ Lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok/)

### Steps for development setup (for the first checkout)

To set up a local development setup, please proceed as follows:

1. Checkout this repository:

   ```bash
   git clone git@github.com:terrestris/shogun.git
   ```

2. Checkout the `shogun-docker` repository:

   ```bash
   git clone git@github.com:terrestris/shogun-docker.git
   ```

3. Create a new project in IntelliJ by importing the first module:

   - `File` -> `New` -> `Project from Existing Sources…`
   - Navigate to the checkout of `shogun`
   - Select the Project Object Model file (`pom.xml`) of `shogun`

4. *Optional:* You may also want to import the `shogun-docker` project.
   If so, import the folder as a module:

   - `File` -> `New` -> `Module from Existing Sources…`
   - Navigate to checkout of `shogun-docker` and choose the directory

5. If not already done, the annotation processing of the Lombok plugin must be
   enabled.
   Check the settings for `Lombok` (Enable Lombok plugin for this project) and
   `Annotation Processors` (Enable annotation processing).

6. Set up the shogun-docker requirements as described in [here](https://github.com/terrestris/shogun-docker).

7. Startup the containers (in the `shogun-docker` checkout directory):

   ```bash
   docker compose up
   ```

8. The application is now available at [https://localhost/](https://localhost/).

### Quick startup

If you already have a local development setup, just proceed as follows:

1. Startup the containers (in the `shogun-docker` checkout directory):

   ```bash
   docker compose up
   ```

2. The application is now available at [https://localhost/](https://localhost/).

### Development hints

#### Local install

Install a new version of SHOGun to your local maven repository with

  ```bash
  mvn clean install
  ```

or

   ```bash
   mvn clean install -DskipTests -Djib.skip=true
   ```

#### Application restart

To apply any changes made, a restart of the application is required. A restart can easily be accomplished by
restarting the appropriate container, e.g.:

   ```bash
   docker restart shogun-boot
   ```

#### Remote debugger

To create a remote debugger, a new run configuration in IntelliJ has to be created:

- Open `Edit configurations` in the `Run` menu.
- Add a new `Remote JVM debug` configuration and enter the following properties:
  - Name: `shogun-boot remote debugger`
  - Host: `localhost`
  - Port: `4711` (may be adjusted to the given module/service)
  - Use module classpath: `shogun-boot` (or any other module)

## GeoServer interceptor

To use REST API of GeoServer interceptor it's necessary to create a role
`interceptor_admin` in Keycloak. Users having this role are allowed to use them.

## MVN Report

If you want to create a report with detailed information about the projects
dependencies or similar, you can use this command:

```bash
mvn site -Preporting
```

Just have a look at `/target/site/index.html` afterwards.

## Get an access token programmatically

To get an access token programmatically the following curl can be used (adjust `<EXTERNAL_KEYCLOAK_HOST>`
beforehand):

```bash
curl \
  -v \
  -k \
  -X POST \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=shogun-client' \
  -d 'username=shogun' \
  -d 'password=shogun' \
  'https://<EXTERNAL_KEYCLOAK_HOST>/auth/realms/SHOGun/protocol/openid-connect/token' | jq '.access_token'
```

The usage of `jq` is optional and can be removed if not needed or `jq` is not available.

## Actuator

[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready)
is enabled by default and is available via the `actuator/` endpoints.
The following list demonstrates some use cases:

- List current properties:
  - `https://localhost/actuator/configprops`
- List current status of flyway migrations:
  - `https://localhost/actuator/flyway`
- List current health information:
  - `https://localhost/actuator/health`
- List build and git information:
  - `https://localhost/actuator/info`
- List current log levels:
  - `https://localhost/actuator/loggers`
- List current log level of a specific module:
  - `https://localhost/actuator/loggers/de.terrestris`
- Set log level for a specific module to the desired level (e.g. `DEBUG` for
  `de.terrestris`):

    ```bash
    curl \
      -v \
      -X POST \
      -H 'Authorization: Bearer <TOKEN>' \
      -H 'Content-Type: application/json' \
      -d '{"configuredLevel": "DEBUG"}' \
      'https://localhost/actuator/loggers/de.terrestris'
    ```

- List all available endpoint mappings:
  - `https://localhost/actuator/mappings`

Note: All endpoints are accessible by users with the `ADMIN` role only.

## Release

- To trigger a release, manually run the `Publish` github action 

## Entity Auditing

SHOGun supports auditing of entities, powered by [Hibernate Envers](https://hibernate.org/orm/envers/).

Auditing is enabled by default and can be disabled by setting `spring.jpa.properties.hibernate.integration.envers` to `false`.

### Enabling envers mid-project

If envers is enabled mid-way and there is already data this can result in errors when querying audit data. To fix this, a revision with revision type `0` (created) has to be manually inserted for each existing entity into the respective audit table.

See https://discourse.hibernate.org/t/safe-envers-queries-when-the-audit-history-is-incomplete/771.
