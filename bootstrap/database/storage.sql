create role gastro login inherit createdb createrole;
alter role gastro password 'gastro';
create database gastro_dev owner gastro template template0 LC_COLLATE 'ru_RU.UTF-8' LC_CTYPE 'ru_RU.UTF-8';


-- http://dolzhenko.blogspot.ru/2008/02/postgresql-83-full-text-search.html
-- http://www.postgresql.org/docs/9.2/static/textsearch-controls.html