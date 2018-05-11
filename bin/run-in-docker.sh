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
    DB_PASSWORD                   Defaults to 'password'
  "
}

# script execution flags
GRADLE_CLEAN=false
GRADLE_INSTALL=false
FLYWAY_ENABLED=false

# environment variables
DB_PASSWORD="password"

execute_script() {
  cd $(dirname "$0")/..

  if [ ${GRADLE_CLEAN} = true ]
  then
    echo "Clearing previous build.."
    ./gradlew clean
  fi

  if [ ${GRADLE_INSTALL} = true ]
  then
    echo "Assembling distribution.."
    ./gradlew assemble
  fi

  echo "Assigning environment variables.."

  export FEATURES_DB_PASSWORD=${DB_PASSWORD}

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
        DB_PASSWORD=*) DB_PASSWORD="${2#*=}" ; shift 2 ;;
        *) shift 2 ;;
      esac ;;
    *) execute_script ; break ;;
  esac
done
