# SHOGun-Boot

## Requirements

- maven >= 3.8
- Java 17
- docker / docker-compose
- IntelliJ
- [IntelliJ Lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok/)

## Steps for development setup (for the first checkout)

1. Checkout this repository.

   ```bash
   git clone git@github.com:terrestris/shogun.git
   ```

2. Checkout the `shogun-docker` repository.

   ```bash
   git clone git@github.com:terrestris/shogun-docker.git
   ```

3. Create a new project in IntelliJ by importing the first module:

   - `File` -> `New` -> `Project from Existing Sources…`
   - Navigate to the checkout of `shogun`
   - Select the Project Object Model file (`pom.xml`) of `shogun`

4. Optional: You may also want to import the `shogun-docker` project.
   If so, import the folder as a module:
   
   - `File` -> `New` -> `Module from Existing Sources…`
   - Navigate to checkout of `shogun-docker` and choose the directory

5. Startup the containers (`shogun-docker`)

   ```bash
   docker-compose up
   ```

6. Navigate to the `ApplicationConfig` in the project tree for the `shogun-boot`
   module and run it (open context menu and select `Run ApplicationConfig.main()`). 
   The first start may fail as you need to add the following `Program arguments`:
   ```
   --spring.profiles.active=base,boot
   ```
   You may save this `Run/Debug configuration` via the dialog box to restart the
   application easily.

7. If not already done, the annotation processing of the Lombok plugin must be
   enabled.  
   Check the settings for `Lombok` (Enable Lombok plugin for this project) and
   `Annotation Processors` (Enable annotation processing).

8. The application is now available at the base URL [http://localhost:8080/shogun-boot](http://localhost:8080/shogun-boot)

## Quick startup

1. Startup the containers (`shogun-docker`)

   ```bash
   docker-compose up
   ```

2. Start the application by selecting the `ApplicationConfig` in the run
   configurations combo.

3. **Or:** Navigate to the `shogun-boot` directory and run

   ```bash
   mvn spring-boot:run
   ```

## Development notes

- [Hot swapping](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html)
  is enabled, so you just need to rebuild the project to effectively restart the
  web server.

## Swagger

The swagger generated API documentation is available after the startup at [http://localhost:8080/shogun-boot/swagger-ui/index.html](http://localhost:8080/shogun-boot/swagger-ui/index.html)

## Keycloak

The integrated Keycloak interface is available at [http://localhost:8000/auth/](http://localhost:8000/auth/).
The default login credentials are `admin:shogun`.

## Curls for testing REST CRUD interfaces

**Note:** All requests that do not use the `GET` method must be tagged with a
valid CSRF token

- `GET` (all applications):

```bash
curl \
  -v \
  -u shogun:shogun \
  -X GET \
  http://localhost:8080/shogun-boot/applications
```

- `GET` (application with ID 1):

```bash
curl \
  -v \
  -u shogun:shogun \
  -X GET \
  http://localhost:8080/shogun-boot/applications/1
```

- `POST` (create a new application):

```bash
curl \
  -v \
  -u shogun:shogun \
  -X POST \
  -H 'Content-Type: application/json' \
  -d '@docs/applicationData.json' \
  http://localhost:8080/shogun-boot/applications
```

- `PUT` (update application with ID 1):

```bash
curl \
  -v \
  -u shogun:shogun \
  -X PUT \
  -H 'Content-Type: application/json' \
  -d '@docs/applicationDataPUT.json' \
  http://localhost:8080/shogun-boot/applications/1
```

- `DELETE` (delete application with ID 1):

```bash
curl \
  -v \
  -u shogun:shogun \
  -X DELETE \
  http://localhost:8080/shogun-boot/applications/1
```

## GeoServer interceptor

To use REST API of GeoServer interceptor its necessary to create a role
`interceptor_admin` in Keycloak. Users having this role are allowed to use them.

## MVN Report

If you want to create a report with detailed information about the projects
dependencies etc, you can use this command:

`mvn site -Preporting`

Just have a look at `/target/site/index.html` afterwards.

## Actuator

[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready)
is enabled by default and is available via the `actuator/` endpoints.
The following list demonstrates some use cases:

- List current properties:
  - `http://localhost:8080/shogun-boot/actuator/configprops`
- List current status of flyway migrations:
  - `http://localhost:8080/shogun-boot/actuator/flyway`
- List current health information:
  - `http://localhost:8080/shogun-boot/actuator/health`
- List build and git information:
  - `http://localhost:8080/shogun-boot/actuator/info`
- List current log levels:
  - `http://localhost:8080/shogun-boot/actuator/loggers`
- List current log level of a specific module:

  - `http://localhost:8080/shogun-boot/actuator/loggers/de.terrestris`

- Set log level for a specific module to the desired level (e.g. `DEBUG` for
  `de.terrestris`):

```bash
curl \
  -v \
  -X POST \
  -u shogun:shogun \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}' \
  'http://localhost:8080/shogun-boot/actuator/loggers/de.terrestris'
```

- List all available endpoint mappings:
  - `http://localhost:8080/shogun-boot/actuator/mappings`

Note: All endpoints are accessible by users with the `ADMIN` role only.

## Release

- Checkout the latest master
- Run the release script, e.g.

```bash
#./scripts/release.sh RELEASE_VERSION DEVELOPMENT_VERSION
./scripts/release.sh "3.0.0" "3.0.1-SNAPSHOT"
```

- Go to `Releases` page and publish the newly created release.

## Create a new SHOGun app

There is a SHOGun example app repository at [https://github.com/terrestris/shogun-example-app](https://github.com/terrestris/shogun-example-app)
but for a manual setup please follow these steps:

1. To manually set up a new project based on SHOgun you need to create a new
maven project and check out the shogun-docker project. Replace the `artifactId`
(`shogun-example-app`) with the specific project name:

   ```bash
   mvn -B archetype:generate -DgroupId=de.terrestris -DartifactId=shogun-example-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
   ```

2. Use the current SHOGun project object model version as inheritance, e.g. in
version 5.0.0:

   ```xml
   <parent>
    <groupId>de.terrestris</groupId>
    <artifactId>shogun</artifactId>
    <version>5.0.0</version>
   </parent>
   ```

3. Include `shogun-boot` for the `dependency` in the same version as used as in
the parent block.

   ```xml
   <dependencies>
    <dependency>
     <groupId>de.terrestris</groupId>
     <artifactId>shogun-boot</artifactId>
     <version>5.0.0</version>
    </dependency>
   </dependencies>
   ```

4. Additionally define the repository for `shogun`:

   ```xml
   <repositories>
    <repository>
     <id>nexus.terrestris.de</id>
     <url>https://nexus.terrestris.de/repository/public/</url>
    </repository>
   </repositories>
   ```

5. Remove the default App file and create a new `ApplicationConfig` in your
project module (e.g. `shogun-example-app/src/main/java/de/terrestris/shogunexample/config/ShogunExampleAppConfig.java`)
to run the main function.

6. Create an `application.yml` file (in `shogun-example-app/src/main/resources/application.yml`)
to specify the project settings. Adjust the `context-path` to the name of your
app.

7. Start the containers and the application as mentioned
[above](#quick-startup).

8. Integrate your app into the Keycloak clients list as new redirect URI for
`shogun-app` (e.g. `http://localhost:8080/shogun-example-app/*`).

## Entity Auditing

Shogun supports auditing of entities, powered by [Hibernate Envers](https://hibernate.org/orm/envers/).

Auditing is enabled by default and can be disabled by setting `spring.jpa.properties.hibernate.integration.envers` to `false`.

### Enabling envers mid-project

If envers is enabled mid-way and there is already data this can result in errors when querying audit data. To fix this, a revision with revision type `0` (created) has to be manually inserted for each existing entity into the respective audit table.

See https://discourse.hibernate.org/t/safe-envers-queries-when-the-audit-history-is-incomplete/771.