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



INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'fabbakery@yandex.ru', 'd590216aff63bb0057f7dd73d4ef8a407b8c0fd56cc0e75ffca430f05bae3b5385f3d8b42f4700cd', 'COOK', '2015-04-05 19:15:10.578', 'ENABLED', NULL, false, 'Анастасия', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'aroma75@list.ru', '557eae59c575549d6eb21edfdba597479a4bcff7f1e67d9ae1ea85df58d30548fdd566a92e4f853d', 'COOK', '2015-04-14 20:28:22.694', 'ENABLED', NULL, false, 'Алёна', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'krasilova.e@gmail.com', '40e0dc3a0f5e7493531e73d86576c9d98f111cca365120caea2bd18c7c0fc606133e016da12a34ec', 'COOK', '2015-04-12 12:26:07.712', 'ENABLED', NULL, false, 'Екатерина', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'marinel.cakes@mail.ru', '6c189aed8b8b37790e3e34c0ed86d93b4a227397096c584452128f6566588d737ca38fbebd61bccc', 'COOK', '2015-04-12 12:31:44.369', 'ENABLED', NULL, false, 'Татьяна', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'kazam_bakery@mail.ru', '2bcab50f88ae93028ac6d55792fde9cdec49195af4030873c930e177d389222223d576370dc8c94a', 'COOK', '2015-04-12 12:32:15.426', 'ENABLED', NULL, false, 'Ксения', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'mysentiments@mail.ru', 'c8a982b12034040efcbad35a78baa2e1e7c05b9effe94e34a6604b93486a273e4812366cf2987816', 'COOK', '2015-04-12 12:33:51.529', 'ENABLED', NULL, false, 'Mysentiments', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'elvira.lovelycake@gmail.com', '2e4a5767fe06bd95cdbec8f6d324e54f214c11126d9ddb335f58a02426184548055a7098235b6526', 'COOK', '2015-04-14 19:22:02.087', 'ENABLED', NULL, false, 'Эльвира', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'evgeniy.tarasov.p@mail.ru', '8d576150d7f89a16f4e2da318e0a7a9d2373d0a5d67a9abc4cb73089d122f3de071bb617367df78e', 'COOK', '2015-04-14 19:22:23.895', 'ENABLED', NULL, false, 'Евгений', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'nadjago@mail.ru', 'abe24f2d3e8c04e962598f6f75078d8c96f10d482a85da5ad467aeb898a8cf96cdbcc17411514594', 'COOK', '2015-04-14 20:31:27.9', 'ENABLED', NULL, false, 'Надежда', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'cook@cook.com', 'eaa79034a7ce1ceb93af995abe23085fba0188dcf3cc1363ba4bcab892415488551725920818b507', 'COOK', '2015-02-24 19:03:48.722', 'ENABLED', NULL, false, 'Иван Поваров', '3', '2', 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', '2015-05-14 15:39:07.143');
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'info@odry-shop.ru', 'afb7ee6873cccd11be9008cc44c30bce5942ed19b82f9acf261ddd2fec7d437f0de4235959303dde', 'COOK', '2015-03-25 11:57:41.039', 'ENABLED', NULL, false, 'Алексей', NULL, NULL, 0, '/img/avatar-stub.png', '/img/avatar-stub-small.png', '/img/avatar-stub-medium.png', NULL);
INSERT INTO USERS VALUES (nextval('hibernate_sequence'), 'jazzcook@yandex.ru', 'd7e90dab7f89a5233a843c687be039ea18c5f85b710655f09b66a99b1f9ddf6e6e55ee3b6025e750', 'COOK', '2015-03-25 09:17:37.49', 'ENABLED', NULL, false, 'Елизавета Скогорева', NULL, NULL, 0, '/media/AVATAR_44_SIZE3.jpg', '/media/AVATAR_44_SIZE1.jpg', '/media/AVATAR_44_SIZE2.jpg', NULL);