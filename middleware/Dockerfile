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

ENV JBOSS_VERSION 6.4.0
ENV JBOSS_HOME /opt/jboss/EAP-6.4.0

WORKDIR /opt/jboss

# Copy EAP to docker image
COPY EAP-$JBOSS_VERSION.zip .

RUN unzip EAP-$JBOSS_VERSION.zip && rm -rf EAP-$JBOSS_VERSION.zip

# Expose managment console and http/https ports
EXPOSE 8080 8443 9990

# Run EAP
CMD ["EAP-6.4.0/bin/standalone.sh", "--server-config", "standalone-full.xml", "-b", "0.0.0.0"]