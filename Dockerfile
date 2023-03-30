FROM eclipse-temurin:17 AS build

WORKDIR /ph-ee-connector-template

COPY . .

RUN ./gradlew bootJar

FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /ph-ee-connector-template/build/libs/ph-ee-connector-template.jar .

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/ph-ee-connector-template.jar"]