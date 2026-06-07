FROM openjdk:21-jdk-bullseye

WORKDIR /app

VOLUME /tmp

COPY mvnw pom.xml ./
COPY .mvn ./.mvn

RUN ./mvnw dependency:go-offline -B || true

COPY src ./src

RUN chmod +x ./mvnw

RUN ./mvnw package -DskipTests

RUN find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" -exec cp {} app.jar \;

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
