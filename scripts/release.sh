#!/bin/bash
#  SHOGun, https://terrestris.github.io/shogun/
#
#  Copyright © 2020-present terrestris GmbH & Co. KG
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0.txt
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# stop at first command failure
set -e

read -rp "Do you really want to create new release of SHOGun (y/n)? "

# Check if prompted to continue
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# Check if the input parameter RELEASE_VERSION is valid
RELEASE_VERSION="$1"
if [[ ! $RELEASE_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9]+)$ ]]; then
    echo "Error: RELEASE_VERSION must be in X.Y.Z format, but was $RELEASE_VERSION"
    exit 1
fi

# Check if the input parameter DEVELOPMENT_VERSION is valid
DEVELOPMENT_VERSION="$2"
if [[ ! $DEVELOPMENT_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9]+)(\-SNAPSHOT)$ ]]; then
    echo "Error: DEVELOPMENT_VERSION must be in X.Y.Z-SNAPSHOT format, but was $DEVELOPMENT_VERSION"
    exit 1
fi

# Check if JAVA_HOME is set
if [[ -z $JAVA_HOME ]]; then
    echo "Error: JAVA_HOME is not set"
    exit 1
fi

SCRIPTDIR=$(dirname "$0")

pushd "$SCRIPTDIR"/..

mvn release:clean
mvn release:prepare --batch-mode -DreleaseVersion="$RELEASE_VERSION" -DdevelopmentVersion="$DEVELOPMENT_VERSION" -DtagNameFormat=v@{project.version} -Darguments="-DskipTests -Dmaven.javadoc.skip=true"
# Deployment will be handled by GitHub actions
mvn release:perform --batch-mode -Darguments="-Dmaven.deploy.skip=true -Djib.skip=true -Dmaven.javadoc.skip=true"

popd
