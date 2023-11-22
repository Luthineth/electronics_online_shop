FROM maven:3-ibmjava-8 AS build
WORKDIR /app
COPY ./Online-Store /app/Online-Store
RUN mvn -f /app/Online-Store/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:8
WORKDIR /app
COPY --from=build /app/Online-Store/target/Online-Store-0.0.1-SNAPSHOT.jar online-store.jar
ENTRYPOINT ["java", "-jar", "/app/online-store.jar"]
EXPOSE 8080
