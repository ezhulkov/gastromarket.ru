FROM ezhulkov/gm-basic:latest
MAINTAINER Eugene Zhulkov ezhulkov@gmail.com

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" > /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" >> /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys ABF5BD827BD9BF62
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
RUN apt-get update && apt-get install -y \
    python-software-properties \
    oracle-java8-installer \
    oracle-java8-set-default \
    tomcat7

COPY server.xml /etc/tomcat7/server.xml
COPY tomcat7 /etc/default/tomcat7

EXPOSE 8080