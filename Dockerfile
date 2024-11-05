FROM openjdk:17-jdk-slim

USER root

RUN apt-get update && apt-get install -y curl unzip

RUN curl -L -o async-profiler.tar.gz https://github.com/async-profiler/async-profiler/releases/download/v3.0/async-profiler-3.0-linux-arm64.tar.gz

RUN tar -xf async-profiler.tar.gz \
    && rm async-profiler.tar.gz

COPY /build/libs/simple-demo-1.0-SNAPSHOT.jar app.jar
COPY /build/resources/* /

#ENTRYPOINT ["java","-XX:TieredStopAtLevel=1","-XX:+PrintCompilation","-jar","/app.jar"]
ENTRYPOINT ["java","-XX:+PrintCompilation","-jar","/app.jar"]