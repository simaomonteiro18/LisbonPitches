# Fase 1 (build): compila a app com Maven e gera o jar
FROM maven:3.10.0-eclipse-temurin-21-noble AS build
WORKDIR /app

# pom.xml primeiro: as dependencias ficam em cache enquanto o pom nao mudar
COPY pom.xml .
RUN mvn -B dependency:go-offline

# o codigo muda muitas vezes, por isso vem depois
COPY src ./src
RUN mvn -B package -DskipTests

# Fase 2 (runtime): so o JRE e o jar, sem Maven nem codigo-fonte
FROM eclipse-temurin:21-jre-alpine-3.24
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# utilizador sem privilegios: a app nao corre como root
RUN addgroup -S lisbonpitches && adduser -S -G lisbonpitches appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]