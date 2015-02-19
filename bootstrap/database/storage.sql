create role gastro login inherit createdb createrole;
alter role gastro password 'gastro';
create database gastro_prod owner gastro template template0 LC_COLLATE 'ru_RU.UTF-8' LC_CTYPE 'ru_RU.UTF-8';

-- place russian* to /usr/share/postgresql/9.4/tsearch_data

CREATE TEXT SEARCH DICTIONARY russian_ispell (
    TEMPLATE = ispell,
    DictFile = russian,
    AffFile = russian,
    StopWords = russian
);
CREATE TEXT SEARCH CONFIGURATION ru ( COPY = russian );
ALTER TEXT SEARCH CONFIGURATION ru ALTER MAPPING FOR hword, hword_part, word WITH russian_ispell, russian_stem;
select to_tsvector('ru', 'мама мыла раму');

-- http://dolzhenko.blogspot.ru/2008/02/postgresql-83-full-text-search.html
-- http://fmg-www.cs.ucla.edu/geoff/ispell-dictionaries.html#Russian-dicts
-- http://www.postgresql.org/docs/9.2/static/textsearch-controls.html