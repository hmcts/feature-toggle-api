ARG APP_INSIGHTS_AGENT_VERSION=2.5.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.4

# Mandatory!
ENV APP feature-toggle-api.jar

COPY build/libs/$APP /opt/app/
COPY lib/applicationinsights-agent-2.5.1.jar lib/AI-Agent.xml /opt/app/

EXPOSE 8580

CMD ["feature-toggle-api.jar"]
