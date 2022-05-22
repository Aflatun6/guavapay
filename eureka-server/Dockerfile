FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
WORKDIR /opt
ENV PORT 8761
EXPOSE 8761
COPY build/libs/*.jar /opt/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]