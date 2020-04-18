#!/bin/bash

echo $PATH

set -x

./gradlew server:dist && cd server/build/libs && scp server-1.0.jar root@54.38.159.237:./ 

set +x
