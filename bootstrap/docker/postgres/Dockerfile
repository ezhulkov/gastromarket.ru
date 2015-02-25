FROM ezhulkov/gm-basic:latest
MAINTAINER Eugene Zhulkov ezhulkov@gmail.com

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 7FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ wheezy-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN apt-get update && apt-get install -y postgresql-9.4
RUN rm -fr /var/lib/postgresql/9.4/main/*
RUN sudo -u postgres /usr/lib/postgresql/9.4/bin/initdb -D /var/lib/postgresql/9.4/main --locale=ru_RU.UTF-8

COPY russian.affix /usr/share/postgresql/9.4/tsearch_data/russian.affix
COPY russian.dict /usr/share/postgresql/9.4/tsearch_data/russian.dict
COPY russian.stop /usr/share/postgresql/9.4/tsearch_data/russian.stop
COPY postgresql.conf /etc/postgresql/9.4/main/postgresql.conf
COPY init.sql init.sql

RUN /etc/init.d/postgresql start && sudo -u postgres psql -f init.sql postgres

EXPOSE 5432

CMD ["/bin/zsh"]