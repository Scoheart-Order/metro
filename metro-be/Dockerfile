FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# Configure Maven to use Aliyun repository
RUN mkdir -p /root/.m2
RUN echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n\
          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n\
          xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n\
  <mirrors>\n\
    <mirror>\n\
      <id>aliyun-maven</id>\n\
      <name>Aliyun Maven Repository</name>\n\
      <url>https://maven.aliyun.com/repository/public</url>\n\
      <mirrorOf>central</mirrorOf>\n\
    </mirror>\n\
  </mirrors>\n\
</settings>" > /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 