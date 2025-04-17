FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# 接收 Maven 仓库路径参数
ARG MAVEN_OPTS
ENV MAVEN_OPTS=$MAVEN_OPTS

# Configure Maven to use Aliyun repository
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/

COPY pom.xml .
# 预先下载依赖，利用Docker构建缓存
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]