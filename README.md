# SHOGun-Boot

## Requirements

* maven >= 3.5
* Java 11
* docker / docker-compose
* IntelliJ
* [IntelliJ Lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok/)

## Steps for development setup (for the first checkout)

1. Checkout this repository.

```
git clone git@github.com:terrestris/shogun.git
```

1. Checkout the `shogun-docker` repository.

```
git clone git@github.com:terrestris/shogun-docker.git
```

1. Create a new project in IntelliJ by importing the first module:
  * `File` -> `Project from Existing Sources…`
  * Navigate to the checkout of `shogun`
  * Select `Import project form external model` and from the list `Maven`
  * `Import Maven projects automatically` (leave all others as preconfigured)
  * Leave profiles as given
  * Select `de.terretris:shogun:X.Y.Z<-SNAPSHOT>`
  * Select Java 11
  * Specify a name (or leave default) and `Finish`

1. **Note:** If you encounter a warning regarding the Java language level just reopen the 
   module settings and set the language level to 11 for each module in the `Sources` tab.
  
1. Optional: You may also want to import the `shogun-docker` project. If so, just
   follow the steps mentioned above, but select `Create module from existing sources`.
 
1. Startup the containers (`shogun-docker`)

```
docker-compose up
```

1. Navigate to the `ApplicationConfig` in the project tree for the `shogun-boot` module and run it (open context menu and select
   `Run ApplicationConfig.main()`)

1. The application is now available under [http://localhost:8080/shogun-boot](http://localhost:8080/shogun-boot)

## Quick startup

1. Startup the containers (`shogun-docker`)

```
docker-compose up
```

1. Start the application by selecting the `ApplicationConfig` in the run configurations combo.

1. **Or:** Navigate to the `shogun-boot` directory and run

```
mvn spring-boot:run
```

## Development notes

* [Hot swapping](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html) is 
  enabled, so you just need to rebuild the project to effectively restart the web server.

## Swagger

The swagger generated API documentation is available after startup under

```
http://localhost:8080/shogun-boot/swagger-ui.html
```

## Curls for testing REST CRUD interfaces

* `GET` (all applications):

```
curl \
  -v \
  -u admin:shogun \
  -X GET \
  http://localhost:8080/shogun-boot/applications
``` 

* `GET` (application with ID 1):

```
curl \
  -v \
  -u admin:shogun \
  -X GET \
  http://localhost:8080/shogun-boot/applications/1
```  

* `POST` (create a new application):

```
curl \
  -v \
  -u admin:shogun \
  -X POST \
  -H 'Content-Type: application/json' \
  -d '@docs/applicationData.json' \
  http://localhost:8080/shogun-boot/applications
```

* `PUT` (update application with ID 1):

```
curl \
  -v \
  -u admin:shogun \
  -X PUT \
  -H 'Content-Type: application/json' \
  -d '@docs/applicationDataPUT.json' \
  http://localhost:8080/shogun-boot/applications/1
```

* `DELETE` (delete application with ID 1):
 
```
curl \
  -v \
  -u admin:shogun \
  -X DELETE \
  http://localhost:8080/shogun-boot/applications/1
``` 

## MVN Report

If you want to create a report with detailed information about the projects dependencies etc, you can use this command:

`mvn site -Preporting`

Just have a look at `/target/site/index.html` afterwards.

## Release

* Checkout the latest master
* Run the release script, e.g.
```
#./scripts/release.sh RELEASE_VERSION DEVELOPMENT_VERSION
./scripts/release.sh "3.0.0" "3.0.1-SNAPSHOT"
```
* Go to `Releases` page and publish the newly created release.
