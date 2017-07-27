# Pull base ubuntu image.
FROM ubuntu:16.04

MAINTAINER Federico Catinello <fcatinello@gmail.com>

RUN apt-get update && apt-get install -y software-properties-common && apt-get install sudo unzip \
	&& add-apt-repository -y ppa:webupd8team/java  && apt-get update

# Install Java.

RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

RUN apt-get install -y oracle-java8-installer

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle