FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

EXPOSE 9222
ENTRYPOINT ["java", "-jar", "target/ApiGateway-0.0.1-SNAPSHOT.jar"]