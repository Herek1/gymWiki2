FROM tomcat:10.1-jdk21

RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/Client-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/Client.war

EXPOSE 8080