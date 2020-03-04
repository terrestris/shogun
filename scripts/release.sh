#!/bin/bash

# stop at first command failure
set -e

read -rp "Do you really want to create new release of SHOGun (y/n)? "

# Check if prompted to continue
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# Check if the input parameter RELEASE_VERSION is valid
RELEASE_VERSION="$1"
if [[ ! $RELEASE_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9])$ ]]; then
    echo "Error: RELEASE_VERSION must be in X.Y.Z format, but was $RELEASE_VERSION"
    exit 1
fi

# Check if the input parameter DEVELOPMENT_VERSION is valid
DEVELOPMENT_VERSION="$2"
if [[ ! $DEVELOPMENT_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9])(\-SNAPSHOT)$ ]]; then
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
mvn release:prepare --batch-mode -DreleaseVersion="$RELEASE_VERSION" -DdevelopmentVersion="$DEVELOPMENT_VERSION" -DtagNameFormat=v@{project.version} -Darguments="-DskipTests"
# Deployment will be handled by GitHub actions
mvn release:perform --batch-mode -Darguments="-Dmaven.deploy.skip=true"

popd
