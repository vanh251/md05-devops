#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if command -v gradle >/dev/null 2>&1; then
  gradle -b wrapper-bootstrap.gradle wrapper --gradle-version 7.6.4
elif command -v docker >/dev/null 2>&1; then
  docker run --rm --user "$(id -u):$(id -g)"     -e GRADLE_USER_HOME=/tmp/gradle-cache     -v "$PWD:/workspace" -w /workspace     gradle:7.6.4-jdk17     gradle -b wrapper-bootstrap.gradle wrapper --gradle-version 7.6.4
else
  echo 'Cần Docker Desktop đang chạy hoặc Gradle cài sẵn. Xem README.md.' >&2
  exit 1
fi
chmod +x gradlew
echo 'Đã tạo Gradle Wrapper. Có thể chạy ./gradlew clean build'
