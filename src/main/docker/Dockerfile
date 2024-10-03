# Use uma imagem base do Maven para compilar o projeto
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código-fonte e compile o projeto
COPY ./src ./src
RUN mvn clean package

# Use uma imagem base do OpenJDK para rodar o JAR
FROM openjdk:17-jre-slim
WORKDIR /app

# Copie o JAR gerado para o container
COPY --from=build /app/target/crm-api.jar /app/crm-api.jar

# Exponha a porta 8080
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "/app/crm-api.jar"]