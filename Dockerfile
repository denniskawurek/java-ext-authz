FROM openjdk:17

COPY ./build/libs/java-ext-authz-0.0.1-SNAPSHOT-all.jar ./src/

WORKDIR /src

CMD ["java", "-jar", "./java-ext-authz-0.0.1-SNAPSHOT-all.jar"]