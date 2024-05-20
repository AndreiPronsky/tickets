FROM amazoncorretto:17-alpine3.18 as extractor
WORKDIR /app
COPY build/libs/app.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]