FROM openjdk:8-jdk-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        git \
        openssh-client \
        procps && \
    rm -rf /var/lib/apt/lists/*

ADD overviewer-reporter.jar /

USER www-data

ENTRYPOINT ["java", "-jar", "overviewer-reporter.jar"]
