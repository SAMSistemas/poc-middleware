FROM ubuntu:16.04-java-8

WORKDIR /home/federico

# Set eap environment variables
ENV JBOSS_VERSION 6.4.0
ENV JBOSS_HOME /home/federico/EAP-6.4.0

# Copy EAP to docker image
COPY EAP-$JBOSS_VERSION.zip .

RUN unzip EAP-$JBOSS_VERSION.zip && rm -rf EAP-$JBOSS_VERSION.zip

# Expose managment console and http/https ports
EXPOSE 8080 8443 9990

# Run EAP
CMD ["EAP-6.4.0/bin/standalone.sh", "--server-config", "standalone-full.xml", "-b", "0.0.0.0"]