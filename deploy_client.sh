#!/bin/bash

echo $PATH

set -x

./gradlew html:dist && cd html/build/dist && jar -cvf game.war * && `heroku deploy:war game.war --app gdx-game`

set +x
