#!/bin/sh

docker build base/. -t ubuntu:16.04-java-8

docker-compose up --build