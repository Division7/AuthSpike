#!/bin/bash
ENV JDBC_DATABASE_PASSWORD=$(echo "$DATABASE_URL" | cut --delimiter=: -f3 | cut --delimiter=\@ -f1)

ENV JDBC_DATABASE_URL=$(echo "$DATABASE_URL" | cut --delimiter=\@ -f2)

ENV JDBC_DATABASE_USERNAME=postgres

java -jar $1
