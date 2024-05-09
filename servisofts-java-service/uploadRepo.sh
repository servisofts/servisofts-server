#!/bin/bash

# curl --request POST "https://repo.servisofts.com/up/home/servisofts/libs/java" \
#     -F '/servisofts-java-service.jar=@"./servisofts-java-service.jar"'

VERSION_SERVER="1.0.11"
scp ./servisofts-java-service.jar servisofts@192.168.2.2:/home/servisofts/repo/resources/home/servisofts/libs/java/servisofts-java-service.jar
scp ./servisofts-java-service.jar servisofts@192.168.2.2:/home/servisofts/repo/resources/home/servisofts/libs/java/servisofts-java-service-$VERSION_SERVER.jar