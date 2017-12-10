This project is a base library for uEngine customization applications.

To run a default uEngine5 BPM backend server, build https://github.com/TheOpenCloudEngine/uEngine5-default rather than this.

# Run uEngine5 in monolothic architecture

## Running backend server


```
git clone https://github.com/TheOpenCloudEngine/uEngine5-default.git
cd uEngine5-default
mvn spring-boot:run
```

go to localhost:8080

## Running front-end server

You need a npm installation on your machine

```
cd src/main/resources/dev
npm install
npm run dev
```

go to localhost:8081


# Run uEngine5 in microservices architecture

To run uEngine5 in MSA, you need to launch all the micro-services one by one:

```
cd service-registry
mvn spring-boot:run

cd ../definition-service
mvn spring-boot:run -Dserver.port=9001 -Drun.arguments="--spring.profiles.active=msa"

cd ../process-service
mvn spring-boot:run -Dserver.port=9002 -Drun.arguments="--spring.profiles.active=msa"

cd ../proxy
mvn spring-boot:run

cd ../front-end
npm install
npm run dev

```

Or, you can use the docker-compose.yml file:

```
docker stack deploy -c docker-compose.yml uengine5

```

# Documents

## Wiki :

https://github.com/TheOpenCloudEngine/uEngine5-base/wiki

