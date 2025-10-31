#!/bin/bash

set -e

USAGE="use clean|package|test|run [args...]"
 
if [ -z "$1" ]; then
    echo $USAGE
    exit 1
fi

SUBCMD="$1"
shift

case "$SUBCMD" in
    "clean")
        cd kanacheck
        mvn clean
        cd ..
        ;;
    "package")
    cd kanacheck
        mvn package
        cd ..
        ;;
    "run")
        java -jar kanacheck/target/kanacheck-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        ;;
    "test")
    cd kanacheck
        mvn test
        cd ..
        ;;
    *)
        echo $USAGE
        exit 1
        ;;
esac

exit 0
