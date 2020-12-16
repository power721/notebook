#!/usr/bin/env bash

cd web-ui && \
npm run build && \
cd .. && \
mvn clean package
