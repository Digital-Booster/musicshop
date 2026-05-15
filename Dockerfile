# FROM dhi.io/maven:3-jdk25-debian13-dev AS package
FROM maven:3.9-eclipse-temurin-21 AS package
WORKDIR /build
COPY ./src src/
COPY pom.xml pom.xml
RUN mvn package -DskipTests 

FROM eclipse-temurin:21-jre-jammy AS final
COPY --from=package build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]