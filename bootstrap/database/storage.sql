create role gastro login inherit createdb createrole;
alter role gastro password 'gastro';
create database gastro_dev owner gastro template template0 LC_COLLATE 'ru_RU.UTF-8' LC_CTYPE 'ru_RU.UTF-8';