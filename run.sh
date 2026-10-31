#!/usr/bin/env bash
set -e
: "${ELASTICSEARCH_USERNAME:?Set institutional Elasticsearch credentials}"
: "${ELASTICSEARCH_PASSWORD:?Set institutional Elasticsearch credentials}"
echo "clean already created JARs"
rm target/scala-2.11/*.jar

echo "fetch the latest changes from GitHub"

echo "compile and making a fat JAR!"
sbt clean && sbt compile && sbt assembly -mem 2048