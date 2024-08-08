FROM eclipse-temurin:17 AS build

WORKDIR /ph-ee-connector-ams-fineract

COPY . .

RUN if ls build/libs/ph-ee-connector-ams-fineract*.jar  1>  /dev/null 2>&1 ; then echo "Using Already built JAR";  \
    else ./gradlew bootJar; fi

FROM eclipse-temurin:17

COPY --from=build /ph-ee-connector-ams-fineract/build/libs/ph-ee-connector-ams-fineract*.jar /app/ph-ee-connector-ams-fineract.jar

WORKDIR /app

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/ph-ee-connector-ams-fineract.jar"]