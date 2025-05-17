#!/usr/bin/env bash

# PGPASSWORD=mypassword psql -U myappuser -h localhost -p 5433 postgresml

cat users.sql |  psql -U myappuser -h localhost -p 5433 postgresml


