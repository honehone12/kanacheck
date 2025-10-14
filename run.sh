#!/bin/bash

set -e

USAGE="use clean|run [args...]"
 
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
    "run")
        mvn package
        echo "******"
        java -jar target/kanacheck-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        ;;
    *)
        echo $USAGE
        exit 1
        ;;
esac

cd ..
exit 0
