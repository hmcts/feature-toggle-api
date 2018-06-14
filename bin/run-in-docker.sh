#!/usr/bin/env sh

print_help() {
  echo "Script to run docker containers for Feature Toggle API as service

  Usage:

  ./run-in-docker.sh [OPTIONS]

  Options:
    --clean, -c                   Clean and install current state of source code
    --install, -i                 Install current state of source code
    --with-flyway, -f             Run docker compose with flyway enabled
    --param PARAM=, -p PARAM=     Parse script parameter
    --help, -h                    Print this help block

  Available parameters:
    APPINSIGHTS                   Defaults to '00000000-0000-0000-0000-000000000000'
    DB_PASSWORD                   Defaults to 'password'
    IDAM_URL                      Defaults to 'false' - use stub
  "
}

# script execution flags
GRADLE_CLEAN=false
GRADLE_INSTALL=false
FLYWAY_ENABLED=false

# environment variables
APPINSIGHTS="00000000-0000-0000-0000-000000000000"
DB_PASSWORD="password"
IDAM_URL="false"

execute_script() {
  cd $(dirname "$0")/..

  if [ ${GRADLE_CLEAN} = true ]
  then
    echo "Clearing previous build.."
    ./gradlew clean
  fi

  if [ ${GRADLE_INSTALL} = true ]
  then
    echo "Installing distribution.."
    ./gradlew installDist
  fi

  echo "Assigning environment variables.."

  export APPINSIGHTS_INSTRUMENTATIONKEY=${APPINSIGHTS}
  export FEATURES_DB_PASSWORD=${DB_PASSWORD}
  export IDAM_URL=${IDAM_URL}

  echo "Bringing up docker containers.."

  if [ ${FLYWAY_ENABLED} = true ]
  then
    docker-compose -f docker-compose-flyway.yml up
  else
    docker-compose up
  fi
}

while true ; do
  case "$1" in
    -h|--help) print_help ; shift ; break ;;
    -c|--clean) GRADLE_CLEAN=true ; GRADLE_INSTALL=true ; shift ;;
    -i|--install) GRADLE_INSTALL=true ; shift ;;
    -f|--with-flyway) FLYWAY_ENABLED=true ; shift ;;
    -p|--param)
      case "$2" in
        APPINSIGHTS=*) APPINSIGHTS="${2#*=}" ; shift 2 ;;
        DB_PASSWORD=*) DB_PASSWORD="${2#*=}" ; shift 2 ;;
        IDAM_URL=*) IDAM_URL="${2#*=}" ; shift 2 ;;
        *) shift 2 ;;
      esac ;;
    *) execute_script ; break ;;
  esac
done
