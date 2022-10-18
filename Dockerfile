FROM openjdk:17

COPY ./build/libs/grpc-server-by-example-pure-java-0.0.1-SNAPSHOT-all.jar ./src/

WORKDIR /src

CMD ["java", "-jar", "./grpc-server-by-example-pure-java-0.0.1-SNAPSHOT-all.jar"]