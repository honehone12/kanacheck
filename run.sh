#!/bin/bash

set -e

USAGE="use clean|package|test|run [args...]"
 
if [ -z "$1" ]; then
    echo $USAGE
    exit 1
fi

SUBCMD="$1"
shift

cd kanacheck

case "$SUBCMD" in
    "clean")
        mvn clean
        ;;
    "package")
        mvn package
        ;;
    "run")
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

cd ..

exit 0
