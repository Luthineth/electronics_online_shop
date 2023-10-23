FROM maven:3-ibmjava-8 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package

FROM openjdk:8
EXPOSE 8080
COPY --from=build /app/target/Online-Store-0.0.1-SNAPSHOT.jar online-store.jar
WORKDIR /
CMD ["java", "-jar", "online-store.jar"]