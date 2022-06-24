#
# SHOGun, https://terrestris.github.io/shogun/
#
# Copyright © 2020-present terrestris GmbH & Co. KG
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   https://www.apache.org/licenses/LICENSE-2.0.txt
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
FROM openjdk@sha256:d50104b0b5811b60c198a3e5ced23c783a49e0d4f78b99762fca4bfdbbc59dd2

WORKDIR /shogun/shogun-gs-interceptor

RUN microdnf install maven

ENTRYPOINT [ \
  "mvn", \
  "spring-boot:run", \
  "-Dspring-boot.run.profiles=base,interceptor", \
  "-Dspring-boot.run.jvmArguments=-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:4711 -Djdk.serialSetFilterAfterRead=true", \
  # enable this line when using a custom application.yml
  # "-Dspring.config.location=/config/application.yml", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dcom.sun.management.jmxremote", \
  "-Dcom.sun.management.jmxremote.port=9010", \
  "-Dcom.sun.management.jmxremote.rmi.port=9010", \
  "-Dcom.sun.management.jmxremote.local.only=false", \
  "-Dcom.sun.management.jmxremote.authenticate=false", \
  "-Dcom.sun.management.jmxremote.ssl=false" \
]
