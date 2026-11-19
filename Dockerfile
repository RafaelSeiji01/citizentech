
# Etapa 1: compila o projeto com Maven e Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagem final só com o Java para rodar o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/citizentech.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]