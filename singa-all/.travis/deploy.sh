#!/usr/bin/env bash

set -e

# only do deployment, when travis detects a new tag
# tun off so you dont have to update the version for testing travis builds
# if [ ! -z "$TRAVIS_TAG" ]
# then
    # echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    # -DnewVersion=$TRAVIS_TAG
    echo "deploying ..."
    mvn -X --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.3:set -Prelease

    if [ ! -z "$TRAVIS" -a -f "$HOME/.gnupg" ]; then
        shred -v ~/.gnupg/*
        rm -rf ~/.gnupg
    fi

    source .travis/gpg.sh

    mvn clean deploy -X --settings .travis/settings.xml -DskipTests=true --batch-mode --update-snapshots -Prelease

    if [ ! -z "$TRAVIS" ]; then
        shred -v ~/.gnupg/*
        rm -rf ~/.gnupg
    fi
# else
#    echo "not on a tag -> keep snapshot version in pom.xml"
# fi
