--liquibase formatted sql
--changeset author:initial-data

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO person (id, email, password, type, date, status, referrer_id, anonymous, full_name, delivery_address, mobile_phone, bonus, avatar_url, avatar_url_small, avatar_url_medium) VALUES (1, 'ezhulkov@gmail.com', 'aa9304926e54bf318a40dc9cb936e62f86327f987461178b01a85ae5a500bd43639908f104ae7d5f', 'ADMIN', NULL, 'ENABLED', NULL, false, 'Eugene Zhulkov', NULL, NULL, 0, 'https://graph.facebook.com/885115111540609/picture?type=large', 'https://graph.facebook.com/885115111540609/picture', 'https://graph.facebook.com/885115111540609/picture?type=large');


--
-- Data for Name: catalog; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO category (id, name, parent_id) VALUES (1, 'Супы', NULL);
INSERT INTO category (id, name, parent_id) VALUES (2, 'Напитки', NULL);
INSERT INTO category (id, name, parent_id) VALUES (3, 'Выпечка и Десерты', NULL);
INSERT INTO category (id, name, parent_id) VALUES (4, 'Фермерская Продукция', NULL);
INSERT INTO category (id, name, parent_id) VALUES (5, 'Кейтеринг', NULL);
INSERT INTO category (id, name, parent_id) VALUES (6, 'Закуски и Сэндвичи', NULL);
INSERT INTO category (id, name, parent_id) VALUES (7, 'Основные блюда', NULL);
INSERT INTO category (id, name, parent_id) VALUES (8, 'Салаты', NULL);
INSERT INTO category (id, name, parent_id) VALUES (9, 'Борщ', 1);
INSERT INTO category (id, name, parent_id) VALUES (10, 'Бульон', 1);
INSERT INTO category (id, name, parent_id) VALUES (11, 'Супы из морепродуктов', 1);
INSERT INTO category (id, name, parent_id) VALUES (13, 'Крем суп', 1);
INSERT INTO category (id, name, parent_id) VALUES (14, 'Овощные супы', 1);
INSERT INTO category (id, name, parent_id) VALUES (15, 'Рыбные супы', 1);
INSERT INTO category (id, name, parent_id) VALUES (16, 'Холодные супы', 1);
INSERT INTO category (id, name, parent_id) VALUES (17, 'Лапша вок', 1);
INSERT INTO category (id, name, parent_id) VALUES (18, 'Грибные супы', 1);
INSERT INTO category (id, name, parent_id) VALUES (19, 'Овощные', 8);
INSERT INTO category (id, name, parent_id) VALUES (20, 'Рыбные', 8);
INSERT INTO category (id, name, parent_id) VALUES (21, 'Фруктовые', 8);
INSERT INTO category (id, name, parent_id) VALUES (22, 'Мясные', 8);
INSERT INTO category (id, name, parent_id) VALUES (23, 'Чай', 2);
INSERT INTO category (id, name, parent_id) VALUES (25, 'Лимонады', 2);
INSERT INTO category (id, name, parent_id) VALUES (26, 'Соки', 2);
INSERT INTO category (id, name, parent_id) VALUES (27, 'Смузи', 2);
INSERT INTO category (id, name, parent_id) VALUES (24, 'Кофе и Шоколад', 2);
INSERT INTO category (id, name, parent_id) VALUES (28, 'Алкогольные напитки', 2);
INSERT INTO category (id, name, parent_id) VALUES (29, 'Молочные коктейли', 2);
INSERT INTO category (id, name, parent_id) VALUES (31, 'Капкейки', 3);
INSERT INTO category (id, name, parent_id) VALUES (32, 'Торты', 3);
INSERT INTO category (id, name, parent_id) VALUES (33, 'Мороженое', 3);
INSERT INTO category (id, name, parent_id) VALUES (34, 'Пироги', 3);
INSERT INTO category (id, name, parent_id) VALUES (35, 'Маффины', 3);
INSERT INTO category (id, name, parent_id) VALUES (36, 'Булочки', 3);
INSERT INTO category (id, name, parent_id) VALUES (37, 'Вафли', 3);
INSERT INTO category (id, name, parent_id) VALUES (38, 'Пицца', 3);
INSERT INTO category (id, name, parent_id) VALUES (39, 'Хлеб', 3);
INSERT INTO category (id, name, parent_id) VALUES (40, 'Бургеры', 6);
INSERT INTO category (id, name, parent_id) VALUES (41, 'Брускетта', 6);
INSERT INTO category (id, name, parent_id) VALUES (42, 'Сэндвичи', 6);
INSERT INTO category (id, name, parent_id) VALUES (43, 'Тосты', 6);
INSERT INTO category (id, name, parent_id) VALUES (44, 'Канапе', 6);
INSERT INTO category (id, name, parent_id) VALUES (45, 'Роллы и Суши', 6);
INSERT INTO category (id, name, parent_id) VALUES (46, 'Кесадилья', 6);
INSERT INTO category (id, name, parent_id) VALUES (47, 'Хумус', 6);
INSERT INTO category (id, name, parent_id) VALUES (48, 'Фондю', 6);
INSERT INTO category (id, name, parent_id) VALUES (50, 'Мясные блюда', 7);
INSERT INTO category (id, name, parent_id) VALUES (51, 'Рыбные блюда', 7);
INSERT INTO category (id, name, parent_id) VALUES (52, 'Блюда из птицы', 7);
INSERT INTO category (id, name, parent_id) VALUES (53, 'Пельмени и манты', 7);
INSERT INTO category (id, name, parent_id) VALUES (55, 'Рагу', 7);
INSERT INTO category (id, name, parent_id) VALUES (56, 'Паэлья', 7);
INSERT INTO category (id, name, parent_id) VALUES (57, 'Блюда с грибами', 7);
INSERT INTO category (id, name, parent_id) VALUES (58, 'Каши', 7);
INSERT INTO category (id, name, parent_id) VALUES (60, 'Мясо', 4);
INSERT INTO category (id, name, parent_id) VALUES (61, 'Птица', 4);
INSERT INTO category (id, name, parent_id) VALUES (62, 'Рыба', 4);
INSERT INTO category (id, name, parent_id) VALUES (63, 'Молочные продукты', 4);
INSERT INTO category (id, name, parent_id) VALUES (64, 'Овощи и фрукты', 4);
INSERT INTO category (id, name, parent_id) VALUES (65, 'Бакалея', 4);
INSERT INTO category (id, name, parent_id) VALUES (66, 'Заготовки', 4);
INSERT INTO category (id, name, parent_id) VALUES (67, 'Ягоды', 4);
INSERT INTO category (id, name, parent_id) VALUES (68, 'Торты', NULL);
INSERT INTO category (id, name, parent_id) VALUES (69, 'Гарниры', NULL);
INSERT INTO category (id, name, parent_id) VALUES (70, 'Бисквитные торты', 68);
INSERT INTO category (id, name, parent_id) VALUES (71, 'Вафельные торты', 68);
INSERT INTO category (id, name, parent_id) VALUES (72, 'Песочные торты', 68);
INSERT INTO category (id, name, parent_id) VALUES (73, 'Творожные торты', 68);

--
-- Data for Name: property; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO property (id, name, type) VALUES (1, 'Кухня', 'LIST');
INSERT INTO property (id, name, type) VALUES (2, 'Ингредиенты', 'LIST');
INSERT INTO property (id, name, type) VALUES (5, 'Вес', 'NUMBER');
INSERT INTO property (id, name, type) VALUES (6, 'Объем', 'NUMBER');
INSERT INTO property (id, name, type) VALUES (7, 'Калорийность', 'NUMBER');
INSERT INTO property (id, name, type) VALUES (8, 'Предпочтения', 'LIST');
INSERT INTO property (id, name, type) VALUES (9, 'Формат', 'LIST');


--
-- Data for Name: category_property; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO category_property (category_id, property_id) VALUES (7, 5);
INSERT INTO category_property (category_id, property_id) VALUES (7, 1);
INSERT INTO category_property (category_id, property_id) VALUES (7, 6);
INSERT INTO category_property (category_id, property_id) VALUES (7, 2);
INSERT INTO category_property (category_id, property_id) VALUES (7, 7);
INSERT INTO category_property (category_id, property_id) VALUES (7, 8);
INSERT INTO category_property (category_id, property_id) VALUES (6, 5);
INSERT INTO category_property (category_id, property_id) VALUES (6, 1);
INSERT INTO category_property (category_id, property_id) VALUES (6, 6);
INSERT INTO category_property (category_id, property_id) VALUES (6, 2);
INSERT INTO category_property (category_id, property_id) VALUES (6, 7);
INSERT INTO category_property (category_id, property_id) VALUES (6, 8);
INSERT INTO category_property (category_id, property_id) VALUES (2, 1);
INSERT INTO category_property (category_id, property_id) VALUES (2, 6);
INSERT INTO category_property (category_id, property_id) VALUES (2, 2);
INSERT INTO category_property (category_id, property_id) VALUES (2, 7);
INSERT INTO category_property (category_id, property_id) VALUES (2, 8);
INSERT INTO category_property (category_id, property_id) VALUES (3, 5);
INSERT INTO category_property (category_id, property_id) VALUES (3, 1);
INSERT INTO category_property (category_id, property_id) VALUES (3, 2);
INSERT INTO category_property (category_id, property_id) VALUES (3, 7);
INSERT INTO category_property (category_id, property_id) VALUES (3, 8);
INSERT INTO category_property (category_id, property_id) VALUES (8, 5);
INSERT INTO category_property (category_id, property_id) VALUES (8, 1);
INSERT INTO category_property (category_id, property_id) VALUES (8, 2);
INSERT INTO category_property (category_id, property_id) VALUES (8, 7);
INSERT INTO category_property (category_id, property_id) VALUES (8, 8);
INSERT INTO category_property (category_id, property_id) VALUES (1, 1);
INSERT INTO category_property (category_id, property_id) VALUES (1, 6);
INSERT INTO category_property (category_id, property_id) VALUES (1, 2);
INSERT INTO category_property (category_id, property_id) VALUES (1, 7);
INSERT INTO category_property (category_id, property_id) VALUES (1, 8);
INSERT INTO category_property (category_id, property_id) VALUES (9, 1);
INSERT INTO category_property (category_id, property_id) VALUES (9, 6);
INSERT INTO category_property (category_id, property_id) VALUES (9, 2);
INSERT INTO category_property (category_id, property_id) VALUES (9, 7);
INSERT INTO category_property (category_id, property_id) VALUES (9, 8);
INSERT INTO category_property (category_id, property_id) VALUES (10, 1);
INSERT INTO category_property (category_id, property_id) VALUES (10, 6);
INSERT INTO category_property (category_id, property_id) VALUES (10, 2);
INSERT INTO category_property (category_id, property_id) VALUES (10, 7);
INSERT INTO category_property (category_id, property_id) VALUES (10, 8);
INSERT INTO category_property (category_id, property_id) VALUES (11, 1);
INSERT INTO category_property (category_id, property_id) VALUES (11, 6);
INSERT INTO category_property (category_id, property_id) VALUES (11, 2);
INSERT INTO category_property (category_id, property_id) VALUES (11, 7);
INSERT INTO category_property (category_id, property_id) VALUES (11, 8);
INSERT INTO category_property (category_id, property_id) VALUES (13, 1);
INSERT INTO category_property (category_id, property_id) VALUES (13, 6);
INSERT INTO category_property (category_id, property_id) VALUES (13, 2);
INSERT INTO category_property (category_id, property_id) VALUES (13, 7);
INSERT INTO category_property (category_id, property_id) VALUES (13, 8);
INSERT INTO category_property (category_id, property_id) VALUES (14, 1);
INSERT INTO category_property (category_id, property_id) VALUES (14, 6);
INSERT INTO category_property (category_id, property_id) VALUES (14, 2);
INSERT INTO category_property (category_id, property_id) VALUES (14, 7);
INSERT INTO category_property (category_id, property_id) VALUES (14, 8);
INSERT INTO category_property (category_id, property_id) VALUES (15, 1);
INSERT INTO category_property (category_id, property_id) VALUES (15, 6);
INSERT INTO category_property (category_id, property_id) VALUES (15, 2);
INSERT INTO category_property (category_id, property_id) VALUES (15, 7);
INSERT INTO category_property (category_id, property_id) VALUES (15, 8);
INSERT INTO category_property (category_id, property_id) VALUES (16, 1);
INSERT INTO category_property (category_id, property_id) VALUES (16, 6);
INSERT INTO category_property (category_id, property_id) VALUES (16, 2);
INSERT INTO category_property (category_id, property_id) VALUES (16, 7);
INSERT INTO category_property (category_id, property_id) VALUES (16, 8);
INSERT INTO category_property (category_id, property_id) VALUES (17, 1);
INSERT INTO category_property (category_id, property_id) VALUES (17, 6);
INSERT INTO category_property (category_id, property_id) VALUES (17, 2);
INSERT INTO category_property (category_id, property_id) VALUES (17, 7);
INSERT INTO category_property (category_id, property_id) VALUES (17, 8);
INSERT INTO category_property (category_id, property_id) VALUES (18, 1);
INSERT INTO category_property (category_id, property_id) VALUES (18, 6);
INSERT INTO category_property (category_id, property_id) VALUES (18, 2);
INSERT INTO category_property (category_id, property_id) VALUES (18, 7);
INSERT INTO category_property (category_id, property_id) VALUES (18, 8);
INSERT INTO category_property (category_id, property_id) VALUES (19, 5);
INSERT INTO category_property (category_id, property_id) VALUES (19, 1);
INSERT INTO category_property (category_id, property_id) VALUES (19, 2);
INSERT INTO category_property (category_id, property_id) VALUES (19, 7);
INSERT INTO category_property (category_id, property_id) VALUES (19, 8);
INSERT INTO category_property (category_id, property_id) VALUES (20, 5);
INSERT INTO category_property (category_id, property_id) VALUES (20, 1);
INSERT INTO category_property (category_id, property_id) VALUES (20, 2);
INSERT INTO category_property (category_id, property_id) VALUES (20, 7);
INSERT INTO category_property (category_id, property_id) VALUES (20, 8);
INSERT INTO category_property (category_id, property_id) VALUES (21, 5);
INSERT INTO category_property (category_id, property_id) VALUES (21, 1);
INSERT INTO category_property (category_id, property_id) VALUES (21, 2);
INSERT INTO category_property (category_id, property_id) VALUES (21, 7);
INSERT INTO category_property (category_id, property_id) VALUES (21, 8);
INSERT INTO category_property (category_id, property_id) VALUES (22, 5);
INSERT INTO category_property (category_id, property_id) VALUES (22, 1);
INSERT INTO category_property (category_id, property_id) VALUES (22, 2);
INSERT INTO category_property (category_id, property_id) VALUES (22, 7);
INSERT INTO category_property (category_id, property_id) VALUES (22, 8);
INSERT INTO category_property (category_id, property_id) VALUES (23, 1);
INSERT INTO category_property (category_id, property_id) VALUES (23, 6);
INSERT INTO category_property (category_id, property_id) VALUES (23, 2);
INSERT INTO category_property (category_id, property_id) VALUES (24, 1);
INSERT INTO category_property (category_id, property_id) VALUES (24, 6);
INSERT INTO category_property (category_id, property_id) VALUES (24, 2);
INSERT INTO category_property (category_id, property_id) VALUES (25, 1);
INSERT INTO category_property (category_id, property_id) VALUES (25, 6);
INSERT INTO category_property (category_id, property_id) VALUES (25, 2);
INSERT INTO category_property (category_id, property_id) VALUES (26, 1);
INSERT INTO category_property (category_id, property_id) VALUES (26, 6);
INSERT INTO category_property (category_id, property_id) VALUES (26, 2);
INSERT INTO category_property (category_id, property_id) VALUES (27, 1);
INSERT INTO category_property (category_id, property_id) VALUES (27, 6);
INSERT INTO category_property (category_id, property_id) VALUES (27, 2);
INSERT INTO category_property (category_id, property_id) VALUES (28, 1);
INSERT INTO category_property (category_id, property_id) VALUES (28, 6);
INSERT INTO category_property (category_id, property_id) VALUES (28, 2);
INSERT INTO category_property (category_id, property_id) VALUES (29, 1);
INSERT INTO category_property (category_id, property_id) VALUES (29, 6);
INSERT INTO category_property (category_id, property_id) VALUES (29, 2);
INSERT INTO category_property (category_id, property_id) VALUES (31, 5);
INSERT INTO category_property (category_id, property_id) VALUES (31, 2);
INSERT INTO category_property (category_id, property_id) VALUES (31, 7);
INSERT INTO category_property (category_id, property_id) VALUES (31, 8);
INSERT INTO category_property (category_id, property_id) VALUES (32, 5);
INSERT INTO category_property (category_id, property_id) VALUES (32, 2);
INSERT INTO category_property (category_id, property_id) VALUES (32, 7);
INSERT INTO category_property (category_id, property_id) VALUES (32, 8);
INSERT INTO category_property (category_id, property_id) VALUES (33, 6);
INSERT INTO category_property (category_id, property_id) VALUES (33, 2);
INSERT INTO category_property (category_id, property_id) VALUES (33, 7);
INSERT INTO category_property (category_id, property_id) VALUES (33, 8);
INSERT INTO category_property (category_id, property_id) VALUES (34, 5);
INSERT INTO category_property (category_id, property_id) VALUES (34, 1);
INSERT INTO category_property (category_id, property_id) VALUES (34, 2);
INSERT INTO category_property (category_id, property_id) VALUES (34, 7);
INSERT INTO category_property (category_id, property_id) VALUES (34, 8);
INSERT INTO category_property (category_id, property_id) VALUES (35, 5);
INSERT INTO category_property (category_id, property_id) VALUES (35, 2);
INSERT INTO category_property (category_id, property_id) VALUES (35, 7);
INSERT INTO category_property (category_id, property_id) VALUES (35, 8);
INSERT INTO category_property (category_id, property_id) VALUES (36, 5);
INSERT INTO category_property (category_id, property_id) VALUES (36, 1);
INSERT INTO category_property (category_id, property_id) VALUES (36, 2);
INSERT INTO category_property (category_id, property_id) VALUES (36, 7);
INSERT INTO category_property (category_id, property_id) VALUES (36, 8);
INSERT INTO category_property (category_id, property_id) VALUES (37, 5);
INSERT INTO category_property (category_id, property_id) VALUES (37, 1);
INSERT INTO category_property (category_id, property_id) VALUES (37, 2);
INSERT INTO category_property (category_id, property_id) VALUES (37, 7);
INSERT INTO category_property (category_id, property_id) VALUES (37, 8);
INSERT INTO category_property (category_id, property_id) VALUES (38, 5);
INSERT INTO category_property (category_id, property_id) VALUES (38, 1);
INSERT INTO category_property (category_id, property_id) VALUES (38, 2);
INSERT INTO category_property (category_id, property_id) VALUES (38, 7);
INSERT INTO category_property (category_id, property_id) VALUES (38, 8);
INSERT INTO category_property (category_id, property_id) VALUES (39, 5);
INSERT INTO category_property (category_id, property_id) VALUES (39, 1);
INSERT INTO category_property (category_id, property_id) VALUES (39, 2);
INSERT INTO category_property (category_id, property_id) VALUES (39, 7);
INSERT INTO category_property (category_id, property_id) VALUES (39, 8);
INSERT INTO category_property (category_id, property_id) VALUES (40, 5);
INSERT INTO category_property (category_id, property_id) VALUES (40, 1);
INSERT INTO category_property (category_id, property_id) VALUES (40, 2);
INSERT INTO category_property (category_id, property_id) VALUES (40, 7);
INSERT INTO category_property (category_id, property_id) VALUES (40, 8);
INSERT INTO category_property (category_id, property_id) VALUES (41, 5);
INSERT INTO category_property (category_id, property_id) VALUES (41, 1);
INSERT INTO category_property (category_id, property_id) VALUES (41, 2);
INSERT INTO category_property (category_id, property_id) VALUES (41, 7);
INSERT INTO category_property (category_id, property_id) VALUES (41, 8);
INSERT INTO category_property (category_id, property_id) VALUES (42, 5);
INSERT INTO category_property (category_id, property_id) VALUES (42, 1);
INSERT INTO category_property (category_id, property_id) VALUES (42, 2);
INSERT INTO category_property (category_id, property_id) VALUES (42, 7);
INSERT INTO category_property (category_id, property_id) VALUES (42, 8);
INSERT INTO category_property (category_id, property_id) VALUES (43, 5);
INSERT INTO category_property (category_id, property_id) VALUES (43, 1);
INSERT INTO category_property (category_id, property_id) VALUES (43, 2);
INSERT INTO category_property (category_id, property_id) VALUES (43, 7);
INSERT INTO category_property (category_id, property_id) VALUES (43, 8);
INSERT INTO category_property (category_id, property_id) VALUES (44, 5);
INSERT INTO category_property (category_id, property_id) VALUES (44, 1);
INSERT INTO category_property (category_id, property_id) VALUES (44, 2);
INSERT INTO category_property (category_id, property_id) VALUES (44, 7);
INSERT INTO category_property (category_id, property_id) VALUES (44, 8);
INSERT INTO category_property (category_id, property_id) VALUES (45, 5);
INSERT INTO category_property (category_id, property_id) VALUES (45, 1);
INSERT INTO category_property (category_id, property_id) VALUES (45, 2);
INSERT INTO category_property (category_id, property_id) VALUES (45, 7);
INSERT INTO category_property (category_id, property_id) VALUES (45, 8);
INSERT INTO category_property (category_id, property_id) VALUES (46, 5);
INSERT INTO category_property (category_id, property_id) VALUES (46, 1);
INSERT INTO category_property (category_id, property_id) VALUES (46, 2);
INSERT INTO category_property (category_id, property_id) VALUES (46, 7);
INSERT INTO category_property (category_id, property_id) VALUES (46, 8);
INSERT INTO category_property (category_id, property_id) VALUES (47, 5);
INSERT INTO category_property (category_id, property_id) VALUES (47, 1);
INSERT INTO category_property (category_id, property_id) VALUES (47, 2);
INSERT INTO category_property (category_id, property_id) VALUES (47, 7);
INSERT INTO category_property (category_id, property_id) VALUES (47, 8);
INSERT INTO category_property (category_id, property_id) VALUES (48, 5);
INSERT INTO category_property (category_id, property_id) VALUES (48, 1);
INSERT INTO category_property (category_id, property_id) VALUES (48, 6);
INSERT INTO category_property (category_id, property_id) VALUES (48, 2);
INSERT INTO category_property (category_id, property_id) VALUES (48, 7);
INSERT INTO category_property (category_id, property_id) VALUES (48, 8);
INSERT INTO category_property (category_id, property_id) VALUES (50, 5);
INSERT INTO category_property (category_id, property_id) VALUES (50, 1);
INSERT INTO category_property (category_id, property_id) VALUES (50, 2);
INSERT INTO category_property (category_id, property_id) VALUES (50, 7);
INSERT INTO category_property (category_id, property_id) VALUES (50, 8);
INSERT INTO category_property (category_id, property_id) VALUES (51, 5);
INSERT INTO category_property (category_id, property_id) VALUES (51, 1);
INSERT INTO category_property (category_id, property_id) VALUES (51, 2);
INSERT INTO category_property (category_id, property_id) VALUES (51, 7);
INSERT INTO category_property (category_id, property_id) VALUES (51, 8);
INSERT INTO category_property (category_id, property_id) VALUES (52, 5);
INSERT INTO category_property (category_id, property_id) VALUES (52, 1);
INSERT INTO category_property (category_id, property_id) VALUES (52, 2);
INSERT INTO category_property (category_id, property_id) VALUES (52, 7);
INSERT INTO category_property (category_id, property_id) VALUES (52, 8);
INSERT INTO category_property (category_id, property_id) VALUES (53, 5);
INSERT INTO category_property (category_id, property_id) VALUES (53, 1);
INSERT INTO category_property (category_id, property_id) VALUES (53, 2);
INSERT INTO category_property (category_id, property_id) VALUES (53, 7);
INSERT INTO category_property (category_id, property_id) VALUES (53, 8);
INSERT INTO category_property (category_id, property_id) VALUES (54, 5);
INSERT INTO category_property (category_id, property_id) VALUES (54, 1);
INSERT INTO category_property (category_id, property_id) VALUES (54, 2);
INSERT INTO category_property (category_id, property_id) VALUES (54, 7);
INSERT INTO category_property (category_id, property_id) VALUES (54, 8);
INSERT INTO category_property (category_id, property_id) VALUES (55, 5);
INSERT INTO category_property (category_id, property_id) VALUES (55, 1);
INSERT INTO category_property (category_id, property_id) VALUES (55, 2);
INSERT INTO category_property (category_id, property_id) VALUES (55, 7);
INSERT INTO category_property (category_id, property_id) VALUES (55, 8);
INSERT INTO category_property (category_id, property_id) VALUES (56, 5);
INSERT INTO category_property (category_id, property_id) VALUES (56, 1);
INSERT INTO category_property (category_id, property_id) VALUES (56, 2);
INSERT INTO category_property (category_id, property_id) VALUES (56, 7);
INSERT INTO category_property (category_id, property_id) VALUES (56, 8);
INSERT INTO category_property (category_id, property_id) VALUES (57, 5);
INSERT INTO category_property (category_id, property_id) VALUES (57, 1);
INSERT INTO category_property (category_id, property_id) VALUES (57, 2);
INSERT INTO category_property (category_id, property_id) VALUES (57, 7);
INSERT INTO category_property (category_id, property_id) VALUES (57, 8);
INSERT INTO category_property (category_id, property_id) VALUES (58, 5);
INSERT INTO category_property (category_id, property_id) VALUES (58, 1);
INSERT INTO category_property (category_id, property_id) VALUES (58, 2);
INSERT INTO category_property (category_id, property_id) VALUES (58, 7);
INSERT INTO category_property (category_id, property_id) VALUES (58, 8);
INSERT INTO category_property (category_id, property_id) VALUES (5, 5);
INSERT INTO category_property (category_id, property_id) VALUES (5, 1);
INSERT INTO category_property (category_id, property_id) VALUES (5, 6);
INSERT INTO category_property (category_id, property_id) VALUES (5, 2);
INSERT INTO category_property (category_id, property_id) VALUES (5, 7);
INSERT INTO category_property (category_id, property_id) VALUES (5, 8);
INSERT INTO category_property (category_id, property_id) VALUES (1, 9);
INSERT INTO category_property (category_id, property_id) VALUES (2, 9);
INSERT INTO category_property (category_id, property_id) VALUES (3, 9);
INSERT INTO category_property (category_id, property_id) VALUES (5, 9);
INSERT INTO category_property (category_id, property_id) VALUES (6, 9);
INSERT INTO category_property (category_id, property_id) VALUES (7, 9);
INSERT INTO category_property (category_id, property_id) VALUES (8, 9);
INSERT INTO category_property (category_id, property_id) VALUES (9, 9);
INSERT INTO category_property (category_id, property_id) VALUES (10, 9);
INSERT INTO category_property (category_id, property_id) VALUES (11, 9);
INSERT INTO category_property (category_id, property_id) VALUES (13, 9);
INSERT INTO category_property (category_id, property_id) VALUES (14, 9);
INSERT INTO category_property (category_id, property_id) VALUES (15, 9);
INSERT INTO category_property (category_id, property_id) VALUES (16, 9);
INSERT INTO category_property (category_id, property_id) VALUES (17, 9);
INSERT INTO category_property (category_id, property_id) VALUES (18, 9);
INSERT INTO category_property (category_id, property_id) VALUES (19, 9);
INSERT INTO category_property (category_id, property_id) VALUES (20, 9);
INSERT INTO category_property (category_id, property_id) VALUES (21, 9);
INSERT INTO category_property (category_id, property_id) VALUES (22, 9);
INSERT INTO category_property (category_id, property_id) VALUES (23, 9);
INSERT INTO category_property (category_id, property_id) VALUES (25, 9);
INSERT INTO category_property (category_id, property_id) VALUES (26, 9);
INSERT INTO category_property (category_id, property_id) VALUES (27, 9);
INSERT INTO category_property (category_id, property_id) VALUES (24, 9);
INSERT INTO category_property (category_id, property_id) VALUES (28, 9);
INSERT INTO category_property (category_id, property_id) VALUES (29, 9);
INSERT INTO category_property (category_id, property_id) VALUES (31, 9);
INSERT INTO category_property (category_id, property_id) VALUES (32, 9);
INSERT INTO category_property (category_id, property_id) VALUES (33, 9);
INSERT INTO category_property (category_id, property_id) VALUES (34, 9);
INSERT INTO category_property (category_id, property_id) VALUES (35, 9);
INSERT INTO category_property (category_id, property_id) VALUES (36, 9);
INSERT INTO category_property (category_id, property_id) VALUES (37, 9);
INSERT INTO category_property (category_id, property_id) VALUES (38, 9);
INSERT INTO category_property (category_id, property_id) VALUES (39, 9);
INSERT INTO category_property (category_id, property_id) VALUES (40, 9);
INSERT INTO category_property (category_id, property_id) VALUES (41, 9);
INSERT INTO category_property (category_id, property_id) VALUES (42, 9);
INSERT INTO category_property (category_id, property_id) VALUES (43, 9);
INSERT INTO category_property (category_id, property_id) VALUES (44, 9);
INSERT INTO category_property (category_id, property_id) VALUES (45, 9);
INSERT INTO category_property (category_id, property_id) VALUES (46, 9);
INSERT INTO category_property (category_id, property_id) VALUES (47, 9);
INSERT INTO category_property (category_id, property_id) VALUES (48, 9);
INSERT INTO category_property (category_id, property_id) VALUES (50, 9);
INSERT INTO category_property (category_id, property_id) VALUES (51, 9);
INSERT INTO category_property (category_id, property_id) VALUES (52, 9);
INSERT INTO category_property (category_id, property_id) VALUES (4, 5);
INSERT INTO category_property (category_id, property_id) VALUES (4, 1);
INSERT INTO category_property (category_id, property_id) VALUES (4, 6);
INSERT INTO category_property (category_id, property_id) VALUES (4, 2);
INSERT INTO category_property (category_id, property_id) VALUES (4, 7);
INSERT INTO category_property (category_id, property_id) VALUES (4, 8);
INSERT INTO category_property (category_id, property_id) VALUES (53, 9);
INSERT INTO category_property (category_id, property_id) VALUES (54, 9);
INSERT INTO category_property (category_id, property_id) VALUES (55, 9);
INSERT INTO category_property (category_id, property_id) VALUES (56, 9);
INSERT INTO category_property (category_id, property_id) VALUES (57, 9);
INSERT INTO category_property (category_id, property_id) VALUES (58, 9);
INSERT INTO category_property (category_id, property_id) VALUES (60, 9);
INSERT INTO category_property (category_id, property_id) VALUES (61, 9);
INSERT INTO category_property (category_id, property_id) VALUES (62, 9);
INSERT INTO category_property (category_id, property_id) VALUES (63, 9);
INSERT INTO category_property (category_id, property_id) VALUES (64, 9);
INSERT INTO category_property (category_id, property_id) VALUES (65, 9);
INSERT INTO category_property (category_id, property_id) VALUES (66, 9);
INSERT INTO category_property (category_id, property_id) VALUES (67, 9);

INSERT INTO category_property (category_id, property_id) VALUES (68, 5);
INSERT INTO category_property (category_id, property_id) VALUES (68, 1);
INSERT INTO category_property (category_id, property_id) VALUES (68, 9);
INSERT INTO category_property (category_id, property_id) VALUES (68, 2);
INSERT INTO category_property (category_id, property_id) VALUES (68, 7);
INSERT INTO category_property (category_id, property_id) VALUES (68, 8);
INSERT INTO category_property (category_id, property_id) VALUES (69, 5);
INSERT INTO category_property (category_id, property_id) VALUES (69, 1);
INSERT INTO category_property (category_id, property_id) VALUES (69, 9);
INSERT INTO category_property (category_id, property_id) VALUES (69, 2);
INSERT INTO category_property (category_id, property_id) VALUES (69, 7);
INSERT INTO category_property (category_id, property_id) VALUES (69, 8);
INSERT INTO category_property (category_id, property_id) VALUES (70, 5);
INSERT INTO category_property (category_id, property_id) VALUES (70, 1);
INSERT INTO category_property (category_id, property_id) VALUES (70, 9);
INSERT INTO category_property (category_id, property_id) VALUES (70, 2);
INSERT INTO category_property (category_id, property_id) VALUES (70, 7);
INSERT INTO category_property (category_id, property_id) VALUES (70, 8);
INSERT INTO category_property (category_id, property_id) VALUES (71, 5);
INSERT INTO category_property (category_id, property_id) VALUES (71, 1);
INSERT INTO category_property (category_id, property_id) VALUES (71, 9);
INSERT INTO category_property (category_id, property_id) VALUES (71, 2);
INSERT INTO category_property (category_id, property_id) VALUES (71, 7);
INSERT INTO category_property (category_id, property_id) VALUES (71, 8);
INSERT INTO category_property (category_id, property_id) VALUES (72, 5);
INSERT INTO category_property (category_id, property_id) VALUES (72, 1);
INSERT INTO category_property (category_id, property_id) VALUES (72, 9);
INSERT INTO category_property (category_id, property_id) VALUES (72, 2);
INSERT INTO category_property (category_id, property_id) VALUES (72, 7);
INSERT INTO category_property (category_id, property_id) VALUES (72, 8);
INSERT INTO category_property (category_id, property_id) VALUES (73, 5);
INSERT INTO category_property (category_id, property_id) VALUES (73, 1);
INSERT INTO category_property (category_id, property_id) VALUES (73, 9);
INSERT INTO category_property (category_id, property_id) VALUES (73, 2);
INSERT INTO category_property (category_id, property_id) VALUES (73, 7);
INSERT INTO category_property (category_id, property_id) VALUES (73, 8);

--
-- Data for Name: hibernate_sequences; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO hibernate_sequences (sequence_name, sequence_next_hi_value) VALUES ('category', 74);
INSERT INTO hibernate_sequences (sequence_name, sequence_next_hi_value) VALUES ('property', 10);
INSERT INTO hibernate_sequences (sequence_name, sequence_next_hi_value) VALUES ('property_value', 267);


--
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: product_property; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: property_value; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO property_value (id, property_id, value) VALUES (1, 1, 'Русская');
INSERT INTO property_value (id, property_id, value) VALUES (2, 1, 'Японская');
INSERT INTO property_value (id, property_id, value) VALUES (3, 1, 'Китайская');
INSERT INTO property_value (id, property_id, value) VALUES (4, 1, 'Юго-Восточная Азия');
INSERT INTO property_value (id, property_id, value) VALUES (5, 1, 'Грузинская');
INSERT INTO property_value (id, property_id, value) VALUES (6, 1, 'Армянская');
INSERT INTO property_value (id, property_id, value) VALUES (7, 1, 'Азербайджанская');
INSERT INTO property_value (id, property_id, value) VALUES (8, 1, 'Итальянская');
INSERT INTO property_value (id, property_id, value) VALUES (9, 1, 'Испанская');
INSERT INTO property_value (id, property_id, value) VALUES (10, 1, 'Мексиканская');
INSERT INTO property_value (id, property_id, value) VALUES (11, 1, 'Средиземноморская');
INSERT INTO property_value (id, property_id, value) VALUES (12, 1, 'Французская');
INSERT INTO property_value (id, property_id, value) VALUES (13, 1, 'Узбекская');
INSERT INTO property_value (id, property_id, value) VALUES (15, 8, 'Детское меню');
INSERT INTO property_value (id, property_id, value) VALUES (14, 8, 'Вегетарианская еда');
INSERT INTO property_value (id, property_id, value) VALUES (16, 8, 'Постная еда');
INSERT INTO property_value (id, property_id, value) VALUES (17, 8, 'Низкокалорийная еда');
INSERT INTO property_value (id, property_id, value) VALUES (267, 1, 'Украинская');
INSERT INTO property_value (id, property_id, value) VALUES (268, 8, 'Спортивная диета');
INSERT INTO property_value (id, property_id, value) VALUES (269, 1, 'Осетинская');
INSERT INTO property_value (id, property_id, value) VALUES (270, 9, 'Корпоратив');
INSERT INTO property_value (id, property_id, value) VALUES (272, 9, 'Детские праздники');
INSERT INTO property_value (id, property_id, value) VALUES (271, 9, 'Свадьбы и юбилеи');
INSERT INTO property_value (id, property_id, value) VALUES (273, 9, 'Фуршеты');
INSERT INTO property_value (id, property_id, value) VALUES (274, 9, 'Шведский стол');
INSERT INTO property_value (id, property_id, value) VALUES (18, 2, 'Cыр «альметте» с йогуртом');
INSERT INTO property_value (id, property_id, value) VALUES (19, 2, 'Абрикосы');
INSERT INTO property_value (id, property_id, value) VALUES (20, 2, 'Авокадо');
INSERT INTO property_value (id, property_id, value) VALUES (21, 2, 'Ананас');
INSERT INTO property_value (id, property_id, value) VALUES (22, 2, 'Ананас консервированный');
INSERT INTO property_value (id, property_id, value) VALUES (23, 2, 'Анис (бадьян)');
INSERT INTO property_value (id, property_id, value) VALUES (24, 2, 'Анчоусы');
INSERT INTO property_value (id, property_id, value) VALUES (25, 2, 'Апельсины');
INSERT INTO property_value (id, property_id, value) VALUES (26, 2, 'Багет французский');
INSERT INTO property_value (id, property_id, value) VALUES (27, 2, 'Базилик');
INSERT INTO property_value (id, property_id, value) VALUES (28, 2, 'Баклажаны');
INSERT INTO property_value (id, property_id, value) VALUES (29, 2, 'Баранина');
INSERT INTO property_value (id, property_id, value) VALUES (30, 2, 'Бекон');
INSERT INTO property_value (id, property_id, value) VALUES (31, 2, 'Белокочанная капуста');
INSERT INTO property_value (id, property_id, value) VALUES (32, 2, 'Бисквитная основа для торта');
INSERT INTO property_value (id, property_id, value) VALUES (33, 2, 'Бобы астурийские (фасоль белая)');
INSERT INTO property_value (id, property_id, value) VALUES (34, 2, 'Ваниль');
INSERT INTO property_value (id, property_id, value) VALUES (35, 2, 'Ванильный стручок');
INSERT INTO property_value (id, property_id, value) VALUES (36, 2, 'Варенье');
INSERT INTO property_value (id, property_id, value) VALUES (37, 2, 'Ветчина');
INSERT INTO property_value (id, property_id, value) VALUES (38, 2, 'Вино белое сухое');
INSERT INTO property_value (id, property_id, value) VALUES (39, 2, 'Вишня');
INSERT INTO property_value (id, property_id, value) VALUES (40, 2, 'Вода');
INSERT INTO property_value (id, property_id, value) VALUES (41, 2, 'Говядина');
INSERT INTO property_value (id, property_id, value) VALUES (42, 2, 'Горчица');
INSERT INTO property_value (id, property_id, value) VALUES (43, 2, 'Горчица дижонская');
INSERT INTO property_value (id, property_id, value) VALUES (44, 2, 'Горчица зернистая');
INSERT INTO property_value (id, property_id, value) VALUES (45, 2, 'Грибы белые');
INSERT INTO property_value (id, property_id, value) VALUES (46, 2, 'Грибы свежие');
INSERT INTO property_value (id, property_id, value) VALUES (47, 2, 'Груши');
INSERT INTO property_value (id, property_id, value) VALUES (48, 2, 'Джем ежевичный');
INSERT INTO property_value (id, property_id, value) VALUES (49, 2, 'Джин');
INSERT INTO property_value (id, property_id, value) VALUES (50, 2, 'Дрожжи свежие');
INSERT INTO property_value (id, property_id, value) VALUES (51, 2, 'Дрожжи сухие');
INSERT INTO property_value (id, property_id, value) VALUES (52, 2, 'Дыня');
INSERT INTO property_value (id, property_id, value) VALUES (53, 2, 'Ежевика');
INSERT INTO property_value (id, property_id, value) VALUES (54, 2, 'Ежевика замороженная');
INSERT INTO property_value (id, property_id, value) VALUES (55, 2, 'Ерш речной');
INSERT INTO property_value (id, property_id, value) VALUES (56, 2, 'Желатин');
INSERT INTO property_value (id, property_id, value) VALUES (57, 2, 'Желе из красной смородины');
INSERT INTO property_value (id, property_id, value) VALUES (58, 2, 'Желток яичный');
INSERT INTO property_value (id, property_id, value) VALUES (59, 2, 'Жир');
INSERT INTO property_value (id, property_id, value) VALUES (60, 2, 'Жир бараний');
INSERT INTO property_value (id, property_id, value) VALUES (61, 2, 'Жир говяжий');
INSERT INTO property_value (id, property_id, value) VALUES (62, 2, 'Жир свиной');
INSERT INTO property_value (id, property_id, value) VALUES (63, 2, 'Зеленый горошек');
INSERT INTO property_value (id, property_id, value) VALUES (64, 2, 'Зеленый горошек замороженный');
INSERT INTO property_value (id, property_id, value) VALUES (65, 2, 'Зеленый горошек стручковый');
INSERT INTO property_value (id, property_id, value) VALUES (66, 2, 'Зефир');
INSERT INTO property_value (id, property_id, value) VALUES (67, 2, 'Изюм');
INSERT INTO property_value (id, property_id, value) VALUES (68, 2, 'Икра');
INSERT INTO property_value (id, property_id, value) VALUES (69, 2, 'Икра горбуши');
INSERT INTO property_value (id, property_id, value) VALUES (70, 2, 'Икра морских ежей');
INSERT INTO property_value (id, property_id, value) VALUES (71, 2, 'Икра щуки');
INSERT INTO property_value (id, property_id, value) VALUES (72, 2, 'Имбирь');
INSERT INTO property_value (id, property_id, value) VALUES (73, 2, 'Имбирь тертый');
INSERT INTO property_value (id, property_id, value) VALUES (74, 2, 'Индейка');
INSERT INTO property_value (id, property_id, value) VALUES (75, 2, 'Индейка копченая');
INSERT INTO property_value (id, property_id, value) VALUES (76, 2, 'Инжир');
INSERT INTO property_value (id, property_id, value) VALUES (77, 2, 'Йогурт');
INSERT INTO property_value (id, property_id, value) VALUES (78, 2, 'Йогурт ванильный');
INSERT INTO property_value (id, property_id, value) VALUES (79, 2, 'Йогурт греческий');
INSERT INTO property_value (id, property_id, value) VALUES (80, 2, 'Йогурт клубничный');
INSERT INTO property_value (id, property_id, value) VALUES (81, 2, 'Йогурт малиновый');
INSERT INTO property_value (id, property_id, value) VALUES (82, 2, 'Йогурт натуральный');
INSERT INTO property_value (id, property_id, value) VALUES (83, 2, 'Йогурт натуральный обезжиренный');
INSERT INTO property_value (id, property_id, value) VALUES (84, 2, 'Йогурт фруктовый');
INSERT INTO property_value (id, property_id, value) VALUES (85, 2, 'Йоркская ветчина');
INSERT INTO property_value (id, property_id, value) VALUES (86, 2, 'Капуста краснокочанная');
INSERT INTO property_value (id, property_id, value) VALUES (87, 2, 'Капуста цветная');
INSERT INTO property_value (id, property_id, value) VALUES (88, 2, 'Картофель');
INSERT INTO property_value (id, property_id, value) VALUES (89, 2, 'Картофель египетский молодой');
INSERT INTO property_value (id, property_id, value) VALUES (90, 2, 'Квас хлебный');
INSERT INTO property_value (id, property_id, value) VALUES (91, 2, 'Кефир 1%-ый');
INSERT INTO property_value (id, property_id, value) VALUES (92, 2, 'Кефир 2,5%-ый');
INSERT INTO property_value (id, property_id, value) VALUES (93, 2, 'Кефир 3,2%-ый');
INSERT INTO property_value (id, property_id, value) VALUES (94, 2, 'Килька');
INSERT INTO property_value (id, property_id, value) VALUES (95, 2, 'Кислота лимонная');
INSERT INTO property_value (id, property_id, value) VALUES (96, 2, 'Колбаса деревенская');
INSERT INTO property_value (id, property_id, value) VALUES (97, 2, 'Корень сельдерея');
INSERT INTO property_value (id, property_id, value) VALUES (98, 2, 'Кофе натуральный');
INSERT INTO property_value (id, property_id, value) VALUES (99, 2, 'Креветки очищенные');
INSERT INTO property_value (id, property_id, value) VALUES (100, 2, 'Крупа манная');
INSERT INTO property_value (id, property_id, value) VALUES (101, 2, 'Куриные желудки');
INSERT INTO property_value (id, property_id, value) VALUES (102, 2, 'Лавровый лист');
INSERT INTO property_value (id, property_id, value) VALUES (103, 2, 'Ликер ежевичный');
INSERT INTO property_value (id, property_id, value) VALUES (104, 2, 'Лимоны');
INSERT INTO property_value (id, property_id, value) VALUES (105, 2, 'Листья цикория');
INSERT INTO property_value (id, property_id, value) VALUES (106, 2, 'Лук зеленый');
INSERT INTO property_value (id, property_id, value) VALUES (107, 2, 'Лук репчатый');
INSERT INTO property_value (id, property_id, value) VALUES (108, 2, 'Лук салатный');
INSERT INTO property_value (id, property_id, value) VALUES (109, 2, 'Лук-порей');
INSERT INTO property_value (id, property_id, value) VALUES (110, 2, 'Лук-шалот');
INSERT INTO property_value (id, property_id, value) VALUES (111, 2, 'Майонез');
INSERT INTO property_value (id, property_id, value) VALUES (112, 2, 'Маслины');
INSERT INTO property_value (id, property_id, value) VALUES (113, 2, 'Масло арахисовое');
INSERT INTO property_value (id, property_id, value) VALUES (114, 2, 'Масло оливковое');
INSERT INTO property_value (id, property_id, value) VALUES (115, 2, 'Масло растительное');
INSERT INTO property_value (id, property_id, value) VALUES (116, 2, 'Масло рафинированное');
INSERT INTO property_value (id, property_id, value) VALUES (117, 2, 'Масло сливочное');
INSERT INTO property_value (id, property_id, value) VALUES (118, 2, 'Масло сливочное топленое');
INSERT INTO property_value (id, property_id, value) VALUES (119, 2, 'Мед');
INSERT INTO property_value (id, property_id, value) VALUES (120, 2, 'Мед цветочный');
INSERT INTO property_value (id, property_id, value) VALUES (121, 2, 'Миндаль жареный');
INSERT INTO property_value (id, property_id, value) VALUES (122, 2, 'Молоко');
INSERT INTO property_value (id, property_id, value) VALUES (123, 2, 'Молоко кислое');
INSERT INTO property_value (id, property_id, value) VALUES (124, 2, 'Морковь');
INSERT INTO property_value (id, property_id, value) VALUES (125, 2, 'Мука пшеничная');
INSERT INTO property_value (id, property_id, value) VALUES (126, 2, 'Мясной бульон');
INSERT INTO property_value (id, property_id, value) VALUES (127, 2, 'Настой фруктовый');
INSERT INTO property_value (id, property_id, value) VALUES (128, 2, 'Нога баранья');
INSERT INTO property_value (id, property_id, value) VALUES (129, 2, 'Нога говяжья');
INSERT INTO property_value (id, property_id, value) VALUES (130, 2, 'Нога телячья');
INSERT INTO property_value (id, property_id, value) VALUES (131, 2, 'Ножки куриные');
INSERT INTO property_value (id, property_id, value) VALUES (132, 2, 'Овощной бульон');
INSERT INTO property_value (id, property_id, value) VALUES (133, 2, 'Огурцы маринованные');
INSERT INTO property_value (id, property_id, value) VALUES (134, 2, 'Огурцы соленые');
INSERT INTO property_value (id, property_id, value) VALUES (135, 2, 'Орегано');
INSERT INTO property_value (id, property_id, value) VALUES (136, 2, 'Орех мускатный');
INSERT INTO property_value (id, property_id, value) VALUES (137, 2, 'Орехи');
INSERT INTO property_value (id, property_id, value) VALUES (138, 2, 'Орехи грецкие');
INSERT INTO property_value (id, property_id, value) VALUES (139, 2, 'Паста томатная');
INSERT INTO property_value (id, property_id, value) VALUES (140, 2, 'Перец болгарский');
INSERT INTO property_value (id, property_id, value) VALUES (141, 2, 'Перец желтый свежий');
INSERT INTO property_value (id, property_id, value) VALUES (142, 2, 'Перец зеленый');
INSERT INTO property_value (id, property_id, value) VALUES (143, 2, 'Перец красный молотый');
INSERT INTO property_value (id, property_id, value) VALUES (144, 2, 'Перец розовый');
INSERT INTO property_value (id, property_id, value) VALUES (145, 2, 'Перец халапеньо');
INSERT INTO property_value (id, property_id, value) VALUES (146, 2, 'Перец черный молотый');
INSERT INTO property_value (id, property_id, value) VALUES (147, 2, 'Перец чили');
INSERT INTO property_value (id, property_id, value) VALUES (148, 2, 'Персики');
INSERT INTO property_value (id, property_id, value) VALUES (149, 2, 'Петрушка');
INSERT INTO property_value (id, property_id, value) VALUES (150, 2, 'Печень трески');
INSERT INTO property_value (id, property_id, value) VALUES (151, 2, 'Печенье «юбилейное»');
INSERT INTO property_value (id, property_id, value) VALUES (152, 2, 'Помидоры');
INSERT INTO property_value (id, property_id, value) VALUES (153, 2, 'Помидоры черри');
INSERT INTO property_value (id, property_id, value) VALUES (154, 2, 'Порошок даши');
INSERT INTO property_value (id, property_id, value) VALUES (155, 2, 'Пюре абрикосовое');
INSERT INTO property_value (id, property_id, value) VALUES (156, 2, 'Пюре ежевичное');
INSERT INTO property_value (id, property_id, value) VALUES (157, 2, 'Ревень');
INSERT INTO property_value (id, property_id, value) VALUES (158, 2, 'Редис');
INSERT INTO property_value (id, property_id, value) VALUES (159, 2, 'Редька');
INSERT INTO property_value (id, property_id, value) VALUES (160, 2, 'Репа');
INSERT INTO property_value (id, property_id, value) VALUES (161, 2, 'Рис');
INSERT INTO property_value (id, property_id, value) VALUES (162, 2, 'Рис длиннозерный');
INSERT INTO property_value (id, property_id, value) VALUES (163, 2, 'Ром');
INSERT INTO property_value (id, property_id, value) VALUES (164, 2, 'Рыба холодного копчения');
INSERT INTO property_value (id, property_id, value) VALUES (165, 2, 'Салат зеленый');
INSERT INTO property_value (id, property_id, value) VALUES (166, 2, 'Салатная заправка');
INSERT INTO property_value (id, property_id, value) VALUES (167, 2, 'Салатная смесь «афиша–еда»');
INSERT INTO property_value (id, property_id, value) VALUES (168, 2, 'Сахар');
INSERT INTO property_value (id, property_id, value) VALUES (169, 2, 'Сахар ванильный');
INSERT INTO property_value (id, property_id, value) VALUES (170, 2, 'Свекла');
INSERT INTO property_value (id, property_id, value) VALUES (171, 2, 'Сливки 20%-ные');
INSERT INTO property_value (id, property_id, value) VALUES (172, 2, 'Сливки 30%-ные');
INSERT INTO property_value (id, property_id, value) VALUES (173, 2, 'Сливки 35%-ные');
INSERT INTO property_value (id, property_id, value) VALUES (174, 2, 'Сливки 40%-ные');
INSERT INTO property_value (id, property_id, value) VALUES (175, 2, 'Сок лимонный');
INSERT INTO property_value (id, property_id, value) VALUES (176, 2, 'Сок юдзу');
INSERT INTO property_value (id, property_id, value) VALUES (177, 2, 'Сок яблочный');
INSERT INTO property_value (id, property_id, value) VALUES (178, 2, 'Соль');
INSERT INTO property_value (id, property_id, value) VALUES (179, 2, 'Соус «южный»');
INSERT INTO property_value (id, property_id, value) VALUES (180, 2, 'Соус вустерширский');
INSERT INTO property_value (id, property_id, value) VALUES (181, 2, 'Соус грибной');
INSERT INTO property_value (id, property_id, value) VALUES (182, 2, 'Соус ежевичный');
INSERT INTO property_value (id, property_id, value) VALUES (183, 2, 'Сыр');
INSERT INTO property_value (id, property_id, value) VALUES (184, 2, 'Сыр домашний');
INSERT INTO property_value (id, property_id, value) VALUES (185, 2, 'Сыр пармезан тертый');
INSERT INTO property_value (id, property_id, value) VALUES (186, 2, 'Сыр фета');
INSERT INTO property_value (id, property_id, value) VALUES (187, 2, 'Сыр эдам');
INSERT INTO property_value (id, property_id, value) VALUES (188, 2, 'Сыр эмменталь');
INSERT INTO property_value (id, property_id, value) VALUES (189, 2, 'Творог');
INSERT INTO property_value (id, property_id, value) VALUES (190, 2, 'Творог 1,8%-ый');
INSERT INTO property_value (id, property_id, value) VALUES (191, 2, 'Телятина');
INSERT INTO property_value (id, property_id, value) VALUES (192, 2, 'Тесто дрожжевое');
INSERT INTO property_value (id, property_id, value) VALUES (193, 2, 'Тимьян');
INSERT INTO property_value (id, property_id, value) VALUES (194, 2, 'Тмин');
INSERT INTO property_value (id, property_id, value) VALUES (195, 2, 'Тунец консервированный в собственном соку');
INSERT INTO property_value (id, property_id, value) VALUES (196, 2, 'Угорь');
INSERT INTO property_value (id, property_id, value) VALUES (197, 2, 'Укроп');
INSERT INTO property_value (id, property_id, value) VALUES (198, 2, 'Уксус');
INSERT INTO property_value (id, property_id, value) VALUES (199, 2, 'Уксус бальзамический');
INSERT INTO property_value (id, property_id, value) VALUES (200, 2, 'Уксус винный');
INSERT INTO property_value (id, property_id, value) VALUES (201, 2, 'Уксус рисовый');
INSERT INTO property_value (id, property_id, value) VALUES (202, 2, 'Уксус яблочный');
INSERT INTO property_value (id, property_id, value) VALUES (203, 2, 'Улитки');
INSERT INTO property_value (id, property_id, value) VALUES (204, 2, 'Урюк');
INSERT INTO property_value (id, property_id, value) VALUES (205, 2, 'Утка');
INSERT INTO property_value (id, property_id, value) VALUES (206, 2, 'Фарш мясной');
INSERT INTO property_value (id, property_id, value) VALUES (207, 2, 'Фарш палтуса');
INSERT INTO property_value (id, property_id, value) VALUES (208, 2, 'Фарш свиной');
INSERT INTO property_value (id, property_id, value) VALUES (209, 2, 'Фасоль зеленая стручковая');
INSERT INTO property_value (id, property_id, value) VALUES (210, 2, 'Филе индейки');
INSERT INTO property_value (id, property_id, value) VALUES (211, 2, 'Филе куриное');
INSERT INTO property_value (id, property_id, value) VALUES (212, 2, 'Филе морского черта');
INSERT INTO property_value (id, property_id, value) VALUES (213, 2, 'Филе морской щуки');
INSERT INTO property_value (id, property_id, value) VALUES (214, 2, 'Финики');
INSERT INTO property_value (id, property_id, value) VALUES (215, 2, 'Фрукты');
INSERT INTO property_value (id, property_id, value) VALUES (216, 2, 'Хек');
INSERT INTO property_value (id, property_id, value) VALUES (217, 2, 'Хлеб белый');
INSERT INTO property_value (id, property_id, value) VALUES (218, 2, 'Хлеб бородинский');
INSERT INTO property_value (id, property_id, value) VALUES (219, 2, 'Хлеб содовый');
INSERT INTO property_value (id, property_id, value) VALUES (220, 2, 'Хлопья кукурузные');
INSERT INTO property_value (id, property_id, value) VALUES (221, 2, 'Хмели-сунели');
INSERT INTO property_value (id, property_id, value) VALUES (222, 2, 'Хрен');
INSERT INTO property_value (id, property_id, value) VALUES (223, 2, 'Цветки цукини');
INSERT INTO property_value (id, property_id, value) VALUES (224, 2, 'Цедра апельсина');
INSERT INTO property_value (id, property_id, value) VALUES (225, 2, 'Цедра лимонная');
INSERT INTO property_value (id, property_id, value) VALUES (226, 2, 'Цицмати');
INSERT INTO property_value (id, property_id, value) VALUES (227, 2, 'Цукаты');
INSERT INTO property_value (id, property_id, value) VALUES (228, 2, 'Цукини');
INSERT INTO property_value (id, property_id, value) VALUES (229, 2, 'Цыпленок');
INSERT INTO property_value (id, property_id, value) VALUES (230, 2, 'Чабрец');
INSERT INTO property_value (id, property_id, value) VALUES (231, 2, 'Черника');
INSERT INTO property_value (id, property_id, value) VALUES (232, 2, 'Чернослив');
INSERT INTO property_value (id, property_id, value) VALUES (233, 2, 'Чеснок');
INSERT INTO property_value (id, property_id, value) VALUES (234, 2, 'Чечевица');
INSERT INTO property_value (id, property_id, value) VALUES (235, 2, 'Чоризо');
INSERT INTO property_value (id, property_id, value) VALUES (236, 2, 'Шалфей');
INSERT INTO property_value (id, property_id, value) VALUES (237, 2, 'Шампанское сухое');
INSERT INTO property_value (id, property_id, value) VALUES (238, 2, 'Шампиньоны');
INSERT INTO property_value (id, property_id, value) VALUES (239, 2, 'Шафран');
INSERT INTO property_value (id, property_id, value) VALUES (240, 2, 'Шафран имеретинский');
INSERT INTO property_value (id, property_id, value) VALUES (241, 2, 'Шоколад горький');
INSERT INTO property_value (id, property_id, value) VALUES (242, 2, 'Шпик');
INSERT INTO property_value (id, property_id, value) VALUES (243, 2, 'Шпинат');
INSERT INTO property_value (id, property_id, value) VALUES (244, 2, 'Шпроты');
INSERT INTO property_value (id, property_id, value) VALUES (245, 2, 'Щавель');
INSERT INTO property_value (id, property_id, value) VALUES (246, 2, 'Щавель маринованный');
INSERT INTO property_value (id, property_id, value) VALUES (247, 2, 'Щечки говяжьи');
INSERT INTO property_value (id, property_id, value) VALUES (248, 2, 'Щечки свиные');
INSERT INTO property_value (id, property_id, value) VALUES (249, 2, 'Щечки телячьи');
INSERT INTO property_value (id, property_id, value) VALUES (250, 2, 'Щука');
INSERT INTO property_value (id, property_id, value) VALUES (251, 2, 'Щупальце осьминога');
INSERT INTO property_value (id, property_id, value) VALUES (252, 2, 'Экстракт ванильный');
INSERT INTO property_value (id, property_id, value) VALUES (253, 2, 'Экстракт кофейный');
INSERT INTO property_value (id, property_id, value) VALUES (254, 2, 'Экстракт миндаля');
INSERT INTO property_value (id, property_id, value) VALUES (255, 2, 'Экстракт мясной');
INSERT INTO property_value (id, property_id, value) VALUES (256, 2, 'Эль имбирный');
INSERT INTO property_value (id, property_id, value) VALUES (257, 2, 'Эспрессо');
INSERT INTO property_value (id, property_id, value) VALUES (258, 2, 'Эссенция ванильная');
INSERT INTO property_value (id, property_id, value) VALUES (259, 2, 'Эстрагона листья');
INSERT INTO property_value (id, property_id, value) VALUES (260, 2, 'Яблоки');
INSERT INTO property_value (id, property_id, value) VALUES (261, 2, 'Ягоды замороженные');
INSERT INTO property_value (id, property_id, value) VALUES (262, 2, 'Ягоды свежие');
INSERT INTO property_value (id, property_id, value) VALUES (263, 2, 'Язык говяжий');
INSERT INTO property_value (id, property_id, value) VALUES (264, 2, 'Яичный белок');
INSERT INTO property_value (id, property_id, value) VALUES (265, 2, 'Яйцо куриное');
INSERT INTO property_value (id, property_id, value) VALUES (266, 2, 'Яйцо перепелиное');


--
-- Data for Name: purchase; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: purchase_product; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: rating; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- PostgreSQL database dump complete
--