FROM openjdk:17
VOLUME /tmp
COPY target/user-service.jar user-service.jar
ENTRYPOINT ["java","-jar","/user-service.jar"]
