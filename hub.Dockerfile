FROM gradle:jdk8 as builder

COPY . /home/gradle/src
USER root
RUN chown -R gradle:gradle /home/gradle/src
USER gradle

ENV FLYWAY_NOOP_STRATEGY=false

WORKDIR /home/gradle/src
RUN gradle assemble

FROM openjdk:8-jre-alpine

COPY --from=builder /home/gradle/src/build/libs/feature-toggle-api.jar /opt/app/

WORKDIR /opt/app

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget --spider -q http://localhost:8580/health || exit 1

EXPOSE 8580

ENTRYPOINT exec java ${JAVA_OPTS} -jar "/opt/app/feature-toggle-api.jar"
