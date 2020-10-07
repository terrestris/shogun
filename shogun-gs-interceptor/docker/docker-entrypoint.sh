#!/bin/bash
set -e

if [ x"${JAVA_ENABLE_DEBUG}" != x ] && [ "${JAVA_ENABLE_DEBUG}" != "false" ]; then
  JAVA_DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${JAVA_DEBUG_PORT:-5005}"
fi

java "$JAVA_DEBUG_ARGS" -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar --spring.config.location=/config/application.yml

exec "$@"
