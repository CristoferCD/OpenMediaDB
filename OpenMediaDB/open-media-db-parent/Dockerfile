FROM openjdk:8-jdk-alpine
EXPOSE 7890
ARG JAR_FILE
COPY $JAR_FILE app.jar
ENTRYPOINT ["java","-jar","/app.jar"]