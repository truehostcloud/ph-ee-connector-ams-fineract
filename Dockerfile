FROM eclipse-temurin:17 AS build

WORKDIR /ph-ee-connector-ams-fineract

COPY . .

RUN ./gradlew bootJar

FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /ph-ee-connector-ams-fineract/build/libs/ph-ee-connector-ams-fineract .

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/ph-ee-connector-ams-fineract.jar"]