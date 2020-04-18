#!/bin/bash

echo $PATH

set -x

./gradlew html:dist && cd html/build && cp -r dist/ /d/apache-tomcat-8.5.53/webapps/

set +x
