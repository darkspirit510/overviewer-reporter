FROM openjdk:8-jdk-slim

RUN apt-get update &&
    apt-get install -y --no-install-recommends \
        ca-certificates \
        git \
        openssh-client \
        procps &&
    rm -rf /var/lib/apt/lists/*

WORKDIR /tmp/overviewer-reporter
RUN git clone https://github.com/darkspirit510/overviewer-reporter . &&
    ./gradlew shadowJar &&
    cp build/libs

USER www-data

ENTRYPOINT ["java", "-jar", "build/libs/overviewer-reporter.jar"]
