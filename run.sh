#!/bin/bash

set -e

cd kanacheck

mvn package
echo "******"
java -jar target/kanacheck-1.0-SNAPSHOT-jar-with-dependencies.jar

cd ..
