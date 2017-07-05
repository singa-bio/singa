#!/usr/bin/env bash
echo "$TRAVIS_BRANCH"
echo "$TRAVIS_PULL_REQUEST"
echo "$TRAVIS_TAG"
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ] && [ ! -z "$TRAVIS_TAG" ];  then
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi