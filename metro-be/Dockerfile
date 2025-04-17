FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# Configure Maven to use Aliyun repository
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]