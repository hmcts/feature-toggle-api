ARG APP_INSIGHTS_AGENT_VERSION=2.5.1-BETA
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2

# Mandatory!
ENV APP feature-toggle-api.jar

COPY build/libs/$APP /opt/app/
COPY lib/applicationinsights-agent-2.5.1-BETA.jar lib/AI-Agent.xml /opt/app/

EXPOSE 8580

CMD ["feature-toggle-api.jar"]
