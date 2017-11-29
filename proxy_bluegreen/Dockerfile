FROM openjdk:8u111-jdk-alpine
VOLUME /tmp
ADD /target/uengine-eureka-zuul-1.0-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]