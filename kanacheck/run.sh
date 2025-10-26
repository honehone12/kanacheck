#!/bin/bash

set -e

USAGE="use clean|test|run [args...]"
 
if [ -z "$1" ]; then
    echo $USAGE
    exit 1
fi

SUBCMD="$1"
shift

case "$SUBCMD" in
    "clean")
        mvn clean
        ;;
    "run")
        mvn package
        echo "******"
        java -jar target/kanacheck-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        ;;
    "test")
        mvn test
        ;;
    *)
        echo $USAGE
        exit 1
        ;;
esac

exit 0
