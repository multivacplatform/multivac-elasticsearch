#!/usr/bin/env bash
echo "clean already created JARs"
rm target/scala-2.11/*.jar

echo "fetch the latest changes from GitHub"
git pull

echo "compile and making a fat JAR!"
sbt clean && sbt compile && sbt assembly -mem 2048