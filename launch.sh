#!/bin/bash

# try in deployment server

pkill -f java
pkill -f node
java -jar target/uengine-five-portal-1.0.0-SNAPSHOT.jar
http-server src/main/resources/dist