#!/bin/bash

echo $PATH

set -x

./gradlew server:dist && cd server/build/libs && `heroku deploy:jar server-1.0.jar --app gdx-server`

set +x
