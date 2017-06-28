#!/usr/bin/env bash

set -e

# only do deployment, when travis detects a new tag
# if [ ! -z "$TRAVIS_TAG" ]
# then

    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.3:set -DnewVersion=$0.1.2 --activate-profiles release &

    # Output to the screen every minute to prevent a travis timeout
    export PID=$!
    while [[ `ps -p $PID | tail -n +2` ]]; do
      echo 'Deploying settings'
      sleep 60
    done

    if [ ! -z "$TRAVIS" -a -f "$HOME/.gnupg" ]; then
        shred -v ~/.gnupg/*
        rm -rf ~/.gnupg
    fi

    source .travis/gpg.sh

    mvn clean deploy --settings .travis/settings.xml -DskipTests=true --batch-mode --update-snapshots --activate-profiles release &

    # Output to the screen every minute to prevent a travis timeout
    export PID=$!
    while [[ `ps -p $PID | tail -n +2` ]]; do
      echo 'Deploying'
      sleep 60
    done

    if [ ! -z "$TRAVIS" ]; then
        shred -v ~/.gnupg/*
        rm -rf ~/.gnupg
    fi
# else
#    echo "not on a tag -> keep snapshot version in pom.xml"
# fi
