#!/usr/bin/env bash
# if [ "$TRAVIS_PULL_REQUEST" == 'false' ] && [ ! -z "$TRAVIS_TAG" ];  then
    echo "deploying for tag $TRAVIS_TAG"
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
# fi