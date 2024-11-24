FROM alpine/java:21-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ewm-service.jar
ENTRYPOINT ["java","-jar","/ewm-service.jar"]