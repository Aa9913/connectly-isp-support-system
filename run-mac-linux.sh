#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
echo "Make sure MongoDB service is running and Maven + Java 21 are installed."
mvn spring-boot:run
