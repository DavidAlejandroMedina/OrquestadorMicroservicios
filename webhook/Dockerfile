FROM openjdk:17-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY target/webhook-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]