# SHOGun

SHOGun is an application framework written in Java for building scaleable web 
application backends in the context of geospatial data infrastructures. It can 
be used directly without any specific customizations or highly customized 
to meet the demands of flexible project requirements.

## In a nutshell

SHOGun…

* …is written in Java and supports Java >= 11.
* …is based on top of Spring / Spring Boot.
* …provides a set of configuration entities to manage the contents of a geospatial 
  data infrastructure (e.g. layers, applications, users or groups).
* …provides (secured) web interfaces (REST & GraphQL) for accessing and 
  manipulating these entities (e.g. for creating an application configuration).
* …separates its functionalities into isolated microservices (e.g. for ) and is
  highly scalable.

## Demo

If you want to test the current state of SHOGun, just checkout the exemplary 
[setup](https://github.com/terrestris/shogun-docker).

## OpenAPI documentation

The REST API documentation is available [here](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/terrestris/shogun/gh-pages/api/swagger.json).
