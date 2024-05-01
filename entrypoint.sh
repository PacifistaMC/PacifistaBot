#!/bin/bash
cd /home/container

echo "Get latest pacifistabot.jar"
rm pacifistabot.jar
cp /home/java/pacifistabot.jar pacifistabot.jar

echo "Java version"
java --version

java -jar -Xms150M -XX:MaxRAMPercentage=95.0 /home/java/pacifistabot.jar
