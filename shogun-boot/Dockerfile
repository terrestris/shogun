FROM openjdk:11-jdk-slim

COPY docker/docker-entrypoint.sh /
COPY target/classes/application.yml /config/application.yml
COPY target/shogun-boot-*.jar /opt/app.jar

ENTRYPOINT [ "/docker-entrypoint.sh" ]
