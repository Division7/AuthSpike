#!/bin/bash
EXPORT JDBC_DATABASE_PASSWORD=$(echo "$DATABASE_URL" | cut --delimiter=: -f3 | cut --delimiter=\@ -f1)

EXPORT JDBC_DATABASE_URL=$(echo "$DATABASE_URL" | cut --delimiter=\@ -f2)

EXPORT JDBC_DATABASE_USERNAME=postgres

java -jar $1
