FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# 接收 Maven 仓库路径参数
ARG MAVEN_OPTS
ENV MAVEN_OPTS=$MAVEN_OPTS

# Configure Maven to use Aliyun repository
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/

# 分两步复制并安装依赖，利用Docker缓存层
COPY pom.xml .
# 使用参数中指定的仓库位置，确保缓存可以被重用
RUN mvn dependency:go-offline -Dmaven.repo.local=${MAVEN_OPTS#*-Dmaven.repo.local=}

# 只有当源代码改变时才会重新构建
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.repo.local=${MAVEN_OPTS#*-Dmaven.repo.local=}

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]