FROM openjdk:8-jre

RUN apt-get update && apt-get -y install postgresql-client

WORKDIR /flyway

ENV FLYWAY_VERSION 5.0.7

ADD http://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}.tar.gz flyway-commandline-${FLYWAY_VERSION}.tar.gz

RUN tar -xzf flyway-commandline-${FLYWAY_VERSION}.tar.gz --strip-components=1 \
  && rm flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && ln -s /flyway/flyway /usr/local/bin/flyway

COPY run-flyway.sh /scripts/run-flyway.sh

ENTRYPOINT ["/scripts/run-flyway.sh"]
