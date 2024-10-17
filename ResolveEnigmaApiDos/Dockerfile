FROM openjdk:17-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY target/resolve-enigma-api-dos-1.0.0.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]