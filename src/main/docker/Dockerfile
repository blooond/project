FROM openjdk:11.0.14.1-jre

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} app.jar

COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x ./wait-for-it.sh

ENTRYPOINT ["./wait-for-it.sh", "db:5432", "--", "java", "-jar", "app.jar"]
