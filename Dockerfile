FROM openjdk:8-jre

COPY build/bootScripts/feature-toggle-api /opt/app/bin/

COPY build/libs/feature-toggle-api.jar /opt/app/lib/

WORKDIR /opt/app

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" curl --silent --fail http://localhost:8580/health

EXPOSE 8580

ENTRYPOINT ["/opt/app/bin/feature-toggle-api"]
