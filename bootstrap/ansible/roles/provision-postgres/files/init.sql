CREATE ROLE gastro LOGIN INHERIT CREATEDB CREATEROLE;
ALTER ROLE gastro PASSWORD 'gastro';
CREATE DATABASE gastro OWNER gastro TEMPLATE template0 LC_COLLATE 'ru_RU.UTF-8' LC_CTYPE 'ru_RU.UTF-8';

-- CREATE TEXT SEARCH DICTIONARY russian_ispell (
-- TEMPLATE = ispell,
--   DictFile = russian,
--   AffFile = russian,
--   StopWords = russian
-- );
-- CREATE TEXT SEARCH CONFIGURATION ru ( COPY = russian );
-- ALTER TEXT SEARCH CONFIGURATION ru ALTER MAPPING FOR hword, hword_part, word WITH russian_ispell, russian_stem;
-- SELECT to_tsvector('ru', 'мама мыла раму');

-- http://dolzhenko.blogspot.ru/2008/02/postgresql-83-full-text-search.html
-- http://fmg-www.cs.ucla.edu/geoff/ispell-dictionaries.html#Russian-dicts
-- http://www.postgresql.org/docs/9.2/static/textsearch-controls.html