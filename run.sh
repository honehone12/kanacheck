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
        cd ..
        ;;
    "run")
        mvn package
        echo "******"
        cd ..
        java -jar kanacheck/target/kanacheck-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        ;;
    *)
        echo $USAGE
        exit 1
        ;;
esac

exit 0
