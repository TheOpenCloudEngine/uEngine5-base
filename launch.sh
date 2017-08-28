#!/bin/bash

# try in deployment server

pkill -f java

pkill -f node

nohup mvn spring-boot:run &

nohup http-server src/main/resources/dist -p 8081 &