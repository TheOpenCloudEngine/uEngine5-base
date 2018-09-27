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
# run local kafka
docker-compose -f docker-compose-kafka.yml up -d

cd uengine5-eureka
mvn spring-boot:run

cd ../definition-service
mvn package -B -U
java -jar target/uengine-five-definistion-*-exec.jar --server.port=9091 --spring.profiles.active=msa

cd ../process-service
mvn package -B -U
javar -jar target/uengine-five-process-*-exec.jar --server.port=9092 --spring.profiles.active=msa

cd uengine5-zuul
mvn spring-boot:run

cd ../front-end
npm install
npm run dev

```

Or, you can use the docker-compose.yml file:

```
docker-compose -f docker-compose.yml up -d

```

# Documents

## Wiki :

https://github.com/TheOpenCloudEngine/uEngine5-base/wiki

