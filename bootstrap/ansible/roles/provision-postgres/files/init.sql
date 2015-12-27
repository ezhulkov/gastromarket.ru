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


-- разбить теги с запятыми
SELECT *
FROM (SELECT DISTINCT initcap(trim(unnest(regexp_split_to_array(name, ',')))) AS n
      FROM property_value
      WHERE name LIKE '%,%') ss
WHERE lower(n) NOT IN (SELECT lower(name)
                       FROM property_value);
INSERT INTO property_value
(id, property_id, name, root_value, client_generated)
  SELECT
    nextval('hibernate_sequence'),
    11,
    n,
    TRUE,
    TRUE
  FROM (SELECT DISTINCT initcap(trim(unnest(regexp_split_to_array(name, ',')))) AS n
        FROM property_value
        WHERE name LIKE '%,%') ss
  WHERE lower(n) NOT IN (SELECT lower(name)
                         FROM property_value);
INSERT INTO tags (id, product_id, property_id, value_id)
  SELECT
    nextval('hibernate_sequence'),
    pid,
    11,
    pv2.id
  FROM (
         SELECT
           t.product_id                                               AS pid,
           initcap(trim(unnest(regexp_split_to_array(pv.name, ',')))) AS pvname
         FROM tags t
           JOIN property_value pv ON pv.id = t.value_id
         WHERE pv.name LIKE '%,%') AS ss JOIN property_value pv2 ON lower(pv2.name) = lower(pvname);
DELETE FROM property_value
WHERE name LIKE '%,%';

-- объединить одинаковые теги
DELETE FROM property_value
WHERE id IN (
  SELECT id
  FROM (SELECT
          id,
          name,
          ROW_NUMBER()
          OVER (PARTITION BY lower(trim(name))
            ORDER BY id) AS rnum
        FROM property_value) t
  WHERE t.rnum >= 2
)
UPDATE tags
SET value_id = (SELECT min(pv.id)
                FROM property_value pv
                WHERE name IN (SELECT name
                               FROM property_value pv2
                               WHERE pv2.id = value_id));