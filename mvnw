#!/bin/sh

set -e
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
JAVA_CMD=java
if [ -n "$JAVA_HOME" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
fi
exec "$JAVA_CMD" -Dmaven.multiModuleProjectDirectory="$SCRIPT_DIR" \
  -classpath "$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.jar" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
