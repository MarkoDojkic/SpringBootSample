#!/usr/bin/env bash
set -euo pipefail
mvn test
mvn spring-boot:run -Dspring-boot.run.profiles=hikari
