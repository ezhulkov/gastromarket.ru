--liquibase formatted sql
--changeset author:initial-data

INSERT INTO users (id, email, password, type, date, status, referrer_id, anonymous, full_name, delivery_address, mobile_phone, bonus, avatar_url, avatar_url_small, avatar_url_medium) VALUES
  (1, 'ezhulkov@gmail.com', 'aa9304926e54bf318a40dc9cb936e62f86327f987461178b01a85ae5a500bd43639908f104ae7d5f', 'ADMIN', NULL, 'ENABLED', NULL, FALSE, 'Eugene Zhulkov', NULL, NULL, 0,
   'https://graph.facebook.com/885115111540609/picture?type=large', 'https://graph.facebook.com/885115111540609/picture', 'https://graph.facebook.com/885115111540609/picture?type=large');

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;

SET search_path = public, pg_catalog;

--
-- Data for Name: catalog; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: bill; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: gastro
--

SELECT pg_catalog.setval('hibernate_sequence', 1000, FALSE);


--
-- Data for Name: logs; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: order_product; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: property; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO property (id, name, type, mandatory) VALUES (5, 'Вес', 'NUMBER', FALSE);
INSERT INTO property (id, name, type, mandatory) VALUES (6, 'Объем', 'NUMBER', FALSE);
INSERT INTO property (id, name, type, mandatory) VALUES (7, 'Калорийность', 'NUMBER', FALSE);
INSERT INTO property (id, name, type, mandatory) VALUES (8, 'Предпочтения', 'LIST', FALSE);
INSERT INTO property (id, name, type, mandatory) VALUES (9, 'Формат', 'LIST', FALSE);
INSERT INTO property (id, name, type, mandatory) VALUES (10, 'Категория', 'LIST', TRUE);
INSERT INTO property (id, name, type, mandatory) VALUES (1, 'Кухня', 'LIST', TRUE);
INSERT INTO property (id, name, type, mandatory) VALUES (11, 'Ингредиенты', 'LIST', TRUE);


--
-- Data for Name: property_value; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (276, 1, 'Европейская', NULL, 'evropeiskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (277, 1, 'Американская', NULL, 'amerikanskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (350, 10, 'Зефирные торты', NULL, 'zefirnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (307, 10, 'Овощное рагу', NULL, 'ovoschnoe-ragu', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (312, 10, 'Каши', NULL, 'kashi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (349, 10, 'Мастичные торты', NULL, 'mastichnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (347, 10, 'Песочные торты', NULL, 'pesochnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (348, 10, ' Творожные торты', NULL, 'tvorozhnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (308, 10, 'Бобовые', NULL, 'bobovye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (330, 10, 'Пельмени и манты', NULL, 'pelmeni-i-manty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (328, 10, 'Рыбные блюда', NULL, 'rybnye-blyuda', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (336, 10, 'Борщ', NULL, 'borsch', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (334, 10, 'Мясные салаты', NULL, 'myasnye-salaty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (332, 10, 'Овощные салаты', NULL, 'ovoschnye-salaty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (333, 10, 'Рыбные салаты', NULL, 'rybnye-salaty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (335, 10, 'Фруктовые салаты', NULL, 'fruktovye-salaty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (345, 10, 'Бисквитные торты', NULL, 'biskvitnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (346, 10, 'Вафельные торты', NULL, 'vafelnye-torty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (311, 10, 'Грибы', NULL, 'griby', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (305, 10, 'Картофель', NULL, 'kartofel', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (309, 10, 'Макаронные изделия', NULL, 'makaronnye-izdeliya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (310, 10, 'Овощи гриль', NULL, 'ovoschi-gril', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (306, 10, 'Рис', NULL, 'ris', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (314, 10, 'Брускетта', NULL, 'brusketta', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (313, 10, 'Бургеры', NULL, 'burgery', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (317, 10, 'Канапе', NULL, 'kanape', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (318, 10, 'Роллы и Суши', NULL, 'rolly-i-sushi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (17, 8, 'Низкокалорийное блюдо', NULL, 'nizkokaloriinoe-blyudo', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (14, 8, 'Вегетарианское блюдо', NULL, 'vegetarianskoe-blyudo', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (315, 10, 'Сэндвичи', NULL, 'sendvichi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (316, 10, 'Тосты', NULL, 'tosty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (326, 10, 'Какао', NULL, 'kakao', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (323, 10, 'Кофе', NULL, 'kofe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (320, 10, 'Лимонады', NULL, 'limonady', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (324, 10, 'Молочные коктейли', NULL, 'molochnye-kokteili', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (322, 10, 'Смузи', NULL, 'smuzi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (321, 10, 'Соки', NULL, 'soki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (319, 10, 'Чай', NULL, 'chai', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (325, 10, 'Шоколад', NULL, 'shokolad', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (329, 10, 'Блюда из птицы', NULL, 'blyuda-iz-ptitsy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (327, 10, 'Мясные блюда', NULL, 'myasnye-blyuda', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (331, 10, 'Паэлья', NULL, 'paelya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (337, 10, 'Бульон', NULL, 'bulon', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (344, 10, 'Грибные супы', NULL, 'gribnye-supy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (339, 10, 'Крем суп', NULL, 'krem-sup', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (343, 10, 'Лапша вок', NULL, 'lapsha-vok', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (340, 10, 'Овощные супы', NULL, 'ovoschnye-supy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (341, 10, 'Рыбные супы', NULL, 'rybnye-supy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (338, 10, 'Супы из морепродуктов', NULL, 'supy-iz-moreproduktov', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (342, 10, 'Холодные супы', NULL, 'holodnye-supy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (351, 10, 'Мясо', NULL, 'myaso', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (352, 10, 'Птица', NULL, 'ptitsa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (353, 10, 'Рыба', NULL, 'ryba', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (356, 10, 'Бакалея', NULL, 'bakaleya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (357, 10, 'Заготовки', NULL, 'zagotovki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (354, 10, 'Молочные продукты', NULL, 'molochnye-produkty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (355, 10, 'Овощи и фрукты', NULL, 'ovoschi-i-frukty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (358, 10, 'Ягоды', NULL, 'yagody', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (286, 1, 'Кавказская', NULL, 'kavkazskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (287, 10, 'Выпечка и десерты', 'ROOT', 'vypechka-i-deserty', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (288, 10, 'Гарниры', 'ROOT', 'garniry', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (289, 10, 'Закуски и бургеры', 'ROOT', 'zakuski-i-burgery', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (290, 10, 'Кейтеринг', 'ROOT', 'keitering', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (291, 10, 'Напитки', 'ROOT', 'napitki', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (292, 10, 'Основные блюда', 'ROOT', 'osnovnye-blyuda', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (293, 10, 'Салаты', 'ROOT', 'salaty', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (294, 10, 'Супы', 'ROOT', 'supy', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (295, 10, 'Торты', 'ROOT', 'torty', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (296, 10, 'Фермерская еда', 'ROOT', 'fermerskaya-eda', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (301, 10, 'Булочки', NULL, 'bulochki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (302, 10, 'Вафли', NULL, 'vafli', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (297, 10, 'Капкейки', NULL, 'kapkeiki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (300, 10, 'Маффины', NULL, 'maffiny', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (298, 10, 'Мороженое', NULL, 'morozhenoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (299, 10, 'Пироги', NULL, 'pirogi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (303, 10, 'Пицца', NULL, 'pitstsa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (304, 10, 'Хлеб', NULL, 'hleb', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (268, 8, 'Спортивная диета', NULL, 'sportivnaya-dieta', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (2, 1, 'Японская', NULL, 'yaponskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (267, 1, 'Украинская', NULL, 'ukrainskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (3, 1, 'Китайская', NULL, 'kitaiskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (12, 1, 'Французская', NULL, 'frantsuzskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (9, 1, 'Испанская', NULL, 'ispanskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (7, 1, 'Азербайджанская', NULL, 'azerbaidzhanskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (6, 1, 'Армянская', NULL, 'armyanskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (5, 1, 'Грузинская', NULL, 'gruzinskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (13, 1, 'Узбекская', NULL, 'uzbekskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (279, 1, 'Тайская', NULL, 'taiskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (280, 1, 'Индийская', NULL, 'indiiskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (281, 1, 'Вьетнамская', NULL, 'vetnamskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (283, 1, 'Прибалтийская', NULL, 'pribaltiiskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (284, 1, 'Немецкая', NULL, 'nemetskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (285, 1, 'Восточноевропейская', NULL, 'vostochnoevropeiskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (270, 9, 'Корпоратив', NULL, 'korporativ', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (271, 9, 'Свадьбы и юбилеи', NULL, 'svadby-i-yubilei', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (273, 9, 'Фуршеты', NULL, 'furshety', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (274, 9, 'Шведский стол', NULL, 'shvedskii-stol', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (16, 8, 'Постное блюдо', NULL, 'postnoe-blyudo', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (275, 1, 'Азиатская', NULL, 'aziatskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (400, 11, 'Баранина', NULL, 'baranina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (401, 11, 'Бекон', NULL, 'bekon', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (402, 11, 'Ветчина', NULL, 'vetchina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (403, 11, 'Говядина', NULL, 'govyadina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (404, 11, 'Телятина', NULL, 'telyatina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (406, 11, 'Язык говяжий', NULL, 'yazyk-govyazhii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (407, 11, 'Колбаса деревенская', NULL, 'kolbasa-derevenskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (408, 11, 'Мясной бульон', NULL, 'myasnoi-bulon', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (409, 11, 'Свинина', NULL, 'svinina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (411, 11, 'Шпик', NULL, 'shpik', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (433, 11, 'Угорь', NULL, 'ugor', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (496, 11, 'Салат зеленый', NULL, 'salat-zelenyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (431, 11, 'Сёмга', NULL, 'semga', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (475, 11, 'Лук репчатый', NULL, 'luk-repchatyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (413, 11, 'Сливочный сыр', NULL, 'slivochnyi-syr', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (414, 11, 'Йогурт', NULL, 'iogurt', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (415, 11, 'Молоко', NULL, 'moloko', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (416, 11, 'Сливки', NULL, 'slivki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (417, 11, 'Сыр', NULL, 'syr', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (418, 11, 'Сыр домашний', NULL, 'syr-domashnii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (420, 11, 'Сыр фета', NULL, 'syr-feta', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (421, 11, 'Сыр эдам', NULL, 'syr-edam', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (422, 11, 'Сыр эмменталь', NULL, 'syr-emmental', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (423, 11, 'Творог', NULL, 'tvorog', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (424, 11, 'Мороженое', NULL, 'morozhenoe2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (442, 11, 'Мясо краба', NULL, 'myaso-kraba', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (426, 11, 'Кефир', NULL, 'kefir', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (427, 11, 'Айран', NULL, 'airan', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (438, 11, 'Морская рыба', NULL, 'morskaya-ryba', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (428, 11, 'Масло сливочное', NULL, 'maslo-slivochnoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (429, 11, 'Масло сливочное топленое', NULL, 'maslo-slivochnoe-toplenoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (362, 11, 'Рыба и морские продукты', NULL, 'ryba-i-morskie-produkty', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (430, 11, 'Лосось', NULL, 'losos', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (445, 11, 'Щука', NULL, 'schuka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (484, 11, 'Перец желтый свежий', NULL, 'perets-zheltyi-svezhii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (432, 11, 'Форель', NULL, 'forel', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (486, 11, 'Перец красный молотый', NULL, 'perets-krasnyi-molotyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (434, 11, 'Карп', NULL, 'karp', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (435, 11, 'Сибас', NULL, 'sibas', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (436, 11, 'Дорадо', NULL, 'dorado', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (439, 11, 'Анчоусы', NULL, 'anchousy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (488, 11, 'Перец халапеньо', NULL, 'perets-halapeno', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (440, 11, 'Ёрш речной', NULL, 'ersh-rechnoi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (441, 11, 'Креветки очищенные', NULL, 'krevetki-ochischennye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (494, 11, 'Репа', NULL, 'repa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (446, 11, 'Икра красная', NULL, 'ikra-krasnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (447, 11, 'Хек', NULL, 'hek', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (448, 11, 'Килька', NULL, 'kilka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (492, 11, 'Редис', NULL, 'redis', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (449, 11, 'Тунец консервированный', NULL, 'tunets-konservirovannyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (450, 11, 'Устрицы', NULL, 'ustritsy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (451, 11, 'Филе рыбное', NULL, 'file-rybnoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (452, 11, 'Печень трески', NULL, 'pechen-treski', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (482, 11, 'Овощной бульон', NULL, 'ovoschnoi-bulon', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (453, 11, 'Рыба холодного копчения', NULL, 'ryba-holodnogo-kopcheniya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (454, 11, 'Рыба горячего копчения', NULL, 'ryba-goryachego-kopcheniya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (363, 11, 'Мясо птицы и яйца', NULL, 'myaso-ptitsy-i-yaitsa', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (455, 11, 'Индейка', NULL, 'indeika', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (456, 11, 'Куриное филе', NULL, 'kurinoe-file', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (457, 11, 'Филе птицы', NULL, 'file-ptitsy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (458, 11, 'Утка', NULL, 'utka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (459, 11, 'Яичный белок', NULL, 'yaichnyi-belok', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (460, 11, 'Яйцо куриное', NULL, 'yaitso-kurinoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (461, 11, 'Яйцо перепелиное', NULL, 'yaitso-perepelinoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (462, 11, 'Желток яичный', NULL, 'zheltok-yaichnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (463, 11, 'Субпродукты', NULL, 'subprodukty2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (490, 11, 'Перец чили', NULL, 'perets-chili', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (464, 11, 'Ножки куриные', NULL, 'nozhki-kurinye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (364, 11, 'Овощи и приправы', NULL, 'ovoschi-i-pripravy', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (493, 11, 'Редька', NULL, 'redka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (465, 11, 'Баклажаны', NULL, 'baklazhany', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (467, 11, 'Фасоль', NULL, 'fasol', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (468, 11, 'Зеленый горошек', NULL, 'zelenyi-goroshek', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (483, 11, 'Перец болгарский', NULL, 'perets-bolgarskii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (469, 11, 'Капуста краснокочанная', NULL, 'kapusta-krasnokochannaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (485, 11, 'Перец зеленый', NULL, 'perets-zelenyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (471, 11, 'Картофель', NULL, 'kartofel2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (487, 11, 'Перец розовый', NULL, 'perets-rozovyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (472, 11, 'Имбирь', NULL, 'imbir', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (474, 11, 'Лук зеленый', NULL, 'luk-zelenyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (491, 11, 'Помидоры', NULL, 'pomidory', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (476, 11, 'Лук салатный', NULL, 'luk-salatnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (477, 11, 'Лук-порей', NULL, 'lukporei', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (478, 11, 'Лук-шалот', NULL, 'lukshalot', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (495, 11, 'Огурцы', NULL, 'ogurtsy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (479, 11, 'Шпинат', NULL, 'shpinat', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (480, 11, 'Щавель', NULL, 'schavel', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (588, 11, 'Вино игристое', NULL, 'vino-igristoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (589, 11, 'Вода', NULL, 'voda', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (592, 11, 'Чай черный', NULL, 'chai-chernyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (503, 11, 'Паста томатная', NULL, 'pasta-tomatnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (504, 11, 'Петрушка', NULL, 'petrushka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (505, 11, 'Укроп', NULL, 'ukrop2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (506, 11, 'Тархун', NULL, 'tarhun', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (507, 11, 'Кинза', NULL, 'kinza', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (508, 11, 'Свекла', NULL, 'svekla', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (597, 11, 'Сок яблочный', NULL, 'sok-yablochnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (571, 11, 'Шафран', NULL, 'shafran', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (572, 11, 'Тимьян', NULL, 'timyan', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (573, 11, 'Тмин', NULL, 'tmin', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (574, 11, 'Соль', NULL, 'sol', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (546, 11, 'Урюк', NULL, 'uryuk', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (576, 11, 'Сахар ванильный', NULL, 'sahar-vanilnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (548, 11, 'Нектарины', NULL, 'nektariny', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (549, 11, 'Лимоны', NULL, 'limony', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (579, 11, 'Грецкий орех', NULL, 'gretskii-oreh', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (520, 11, 'Мука пшеничная', NULL, 'muka-pshenichnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (552, 11, 'Горчица', NULL, 'gorchitsa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (582, 11, 'Семена тыквы', NULL, 'semena-tykvy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (522, 11, 'Авокадо', NULL, 'avokado', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (523, 11, 'Ананас', NULL, 'ananas', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (524, 11, 'Апельсины', NULL, 'apelsiny', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (525, 11, 'Бананы', NULL, 'banany', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (558, 11, 'Уксус рисовый', NULL, 'uksus-risovyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (586, 11, 'Вино красное', NULL, 'vino-krasnoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (587, 11, 'Вино розовое', NULL, 'vino-rozovoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (498, 11, 'Хрен', NULL, 'hren', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (499, 11, 'Чеснок', NULL, 'chesnok', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (590, 11, 'Кофе натуральный', NULL, 'kofe-naturalnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (591, 11, 'Квас хлебный', NULL, 'kvas-hlebnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (501, 11, 'Огурцы соленые', NULL, 'ogurtsy-solenye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (502, 11, 'Морковь', NULL, 'morkov', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (593, 11, 'Чай зеленый', NULL, 'chai-zelenyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (594, 11, 'Пиво', NULL, 'pivo', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (595, 11, 'Ром', NULL, 'rom', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (596, 11, 'Сок лимонный', NULL, 'sok-limonnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (365, 11, 'Мучные изделия', NULL, 'muchnye-izdeliya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (509, 11, 'Печенье', NULL, 'pechene', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (510, 11, 'Сдобное тесто', NULL, 'sdobnoe-testo', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (511, 11, 'Песочное тесто', NULL, 'pesochnoe-testo', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (575, 11, 'Сахар', NULL, 'sahar', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (513, 11, 'Багет французский', NULL, 'baget-frantsuzskii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (515, 11, 'Хлеб белый', NULL, 'hleb-belyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (516, 11, 'Хлеб черный', NULL, 'hleb-chernyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (517, 11, 'Хлеб зерновой', NULL, 'hleb-zernovoi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (550, 11, 'Майонез', NULL, 'maionez', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (519, 11, 'Хлеб содовый', NULL, 'hleb-sodovyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (581, 11, 'Семена подсолнечника', NULL, 'semena-podsolnechnika', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (366, 11, 'Фрукты и ягоды', NULL, 'frukty-i-yagody', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (521, 11, 'Абрикосы', NULL, 'abrikosy', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (583, 11, 'Кунжут', NULL, 'kunzhut', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (584, 11, 'Орех мускатный', NULL, 'oreh-muskatnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (369, 11, 'Напитки', NULL, 'napitki2', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (527, 11, 'Киви', NULL, 'kivi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (528, 11, 'Малина', NULL, 'malina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (529, 11, 'Клубника', NULL, 'klubnika', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (530, 11, 'Смородина', NULL, 'smorodina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (531, 11, 'Дыня', NULL, 'dynya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (532, 11, 'Ежевика', NULL, 'ezhevika', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (533, 11, 'Изюм', NULL, 'izyum', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (535, 11, 'Вишня', NULL, 'vishnya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (536, 11, 'Груши', NULL, 'grushi2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (537, 11, 'Яблоки', NULL, 'yabloki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (538, 11, 'Ягоды', NULL, 'yagody2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (539, 11, 'Инжир', NULL, 'inzhir', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (540, 11, 'Маслины', NULL, 'masliny', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (541, 11, 'Цедра апельсина', NULL, 'tsedra-apelsina', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (542, 11, 'Цедра лимонная', NULL, 'tsedra-limonnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (543, 11, 'Черника', NULL, 'chernika', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (545, 11, 'Цукаты', NULL, 'tsukaty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (547, 11, 'Персики', NULL, 'persiki', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (368, 11, 'Орехи и семена', NULL, 'orehi-i-semena', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (577, 11, 'Миндаль', NULL, 'mindal', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (578, 11, 'Арахис', NULL, 'arahis', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (367, 11, 'Соусы и приправы', NULL, 'sousy-i-pripravy', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (580, 11, 'Фундук', NULL, 'funduk', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (551, 11, 'Чесночный соус', NULL, 'chesnochnyi-sous', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (553, 11, 'Базилик', NULL, 'bazilik', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (554, 11, 'Ваниль', NULL, 'vanil', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (555, 11, 'Уксус', NULL, 'uksus', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (585, 11, 'Вино белое', NULL, 'vino-beloe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (557, 11, 'Уксус винный', NULL, 'uksus-vinnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (559, 11, 'Уксус яблочный', NULL, 'uksus-yablochnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (560, 11, 'Дрожжи', NULL, 'drozhzhi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (561, 11, 'Желатин', NULL, 'zhelatin', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (563, 11, 'Лавровый лист', NULL, 'lavrovyi-list', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (564, 11, 'Масло арахисовое', NULL, 'maslo-arahisovoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (565, 11, 'Масло оливковое', NULL, 'maslo-olivkovoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (566, 11, 'Масло растительное', NULL, 'maslo-rastitelnoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (568, 11, 'Листья цикория', NULL, 'listya-tsikoriya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (569, 11, 'Хмели-сунели', NULL, 'hmelisuneli', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (570, 11, 'Шалфей', NULL, 'shalfei', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (370, 11, 'Сладкое', NULL, 'sladkoe', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (1, 1, 'Русская', NULL, 'russkaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (10, 1, 'Мексиканская', NULL, 'meksikanskaya', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (8, 1, 'Итальянская', NULL, 'italyanskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (269, 1, 'Осетинская', NULL, 'osetinskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (282, 1, 'Скандинавская', NULL, 'skandinavskaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (272, 9, 'Детские праздники', NULL, 'detskie-prazdniki', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (360, 11, 'Мясо и мясные продукты', NULL, 'myaso-i-myasnye-produkty', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (405, 11, 'Фарш мясной', NULL, 'farsh-myasnoi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (361, 11, 'Молочные продукты', NULL, 'molochnye-produkty2', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (419, 11, 'Сыр пармезан тертый', NULL, 'syr-parmezan-tertyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (425, 11, 'Ряженка', NULL, 'ryazhenka', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (437, 11, 'Речная рыба', NULL, 'rechnaya-ryba', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (443, 11, 'Шпроты', NULL, 'shproty', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (444, 11, 'Щупальце осьминога', NULL, 'schupaltse-osminoga', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (466, 11, 'Белокочанная капуста', NULL, 'belokochannaya-kapusta', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (470, 11, 'Капуста цветная', NULL, 'kapusta-tsvetnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (473, 11, 'Корень сельдерея', NULL, 'koren-seldereya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (489, 11, 'Перец черный молотый', NULL, 'perets-chernyi-molotyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (497, 11, 'Цукини', NULL, 'tsukini', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (500, 11, 'Огурцы маринованные', NULL, 'ogurtsy-marinovannye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (512, 11, 'Слоеное тесто', NULL, 'sloenoe-testo', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (514, 11, 'Бисквитная основа для торта', NULL, 'biskvitnaya-osnova-dlya-torta', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (518, 11, 'Хлеб бородинский', NULL, 'hleb-borodinskii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (534, 11, 'Виноград', NULL, 'vinograd', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (544, 11, 'Чернослив', NULL, 'chernosliv', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (556, 11, 'Уксус бальзамический', NULL, 'uksus-balzamicheskii', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (562, 11, 'Кислота лимонная', NULL, 'kislota-limonnaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (567, 11, 'Масло рафинированное', NULL, 'maslo-rafinirovannoe', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (602, 11, 'Шоколад белый', NULL, 'shokolad-belyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (603, 11, 'Шоколад молочный', NULL, 'shokolad-molochnyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (604, 11, 'Шоколад чёрный', NULL, 'shokolad-chernyi', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (371, 11, 'Бакалея', NULL, 'bakaleya2', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (15, 8, 'Детское меню', NULL, 'detskoe-menyu', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (598, 11, 'Варенье', NULL, 'varene', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (599, 11, 'Джем', NULL, 'dzhem', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (600, 11, 'Зефир', NULL, 'zefir', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (601, 11, 'Мед', NULL, 'med', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (605, 11, 'Крупа', NULL, 'krupa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (606, 11, 'Макароны', NULL, 'makarony', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (607, 11, 'Спагетти', NULL, 'spagetti', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (608, 11, 'Фарфалле', NULL, 'farfalle', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (609, 11, 'Крупа манная', NULL, 'krupa-mannaya', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (610, 11, 'Гречневая крупа', NULL, 'grechnevaya-krupa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (611, 11, 'Чечевица', NULL, 'chechevitsa', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (612, 11, 'Бобы', NULL, 'boby', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (613, 11, 'Рис', NULL, 'ris2', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (614, 11, 'Хлопья кукурузные', NULL, 'hlopya-kukuruznye', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (372, 11, 'Грибы', NULL, 'griby2', TRUE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (615, 11, 'Шампиньоны', NULL, 'shampinony', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (616, 11, 'Белые грибы', NULL, 'belye-griby', FALSE);
INSERT INTO property_value (id, property_id, name, tag, alt_id, root_value) VALUES (617, 11, 'Вешенки', NULL, 'veshenki', FALSE);


--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: gastro
--



--
-- Data for Name: value_value; Type: TABLE DATA; Schema: public; Owner: gastro
--

INSERT INTO value_value (parent_id, child_id) VALUES (360, 400);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 401);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 402);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 403);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 404);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 405);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 406);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 407);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 408);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 409);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 411);
INSERT INTO value_value (parent_id, child_id) VALUES (360, 463);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 413);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 414);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 415);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 416);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 417);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 418);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 419);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 420);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 421);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 422);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 423);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 424);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 425);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 426);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 427);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 428);
INSERT INTO value_value (parent_id, child_id) VALUES (361, 429);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 430);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 431);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 432);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 433);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 434);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 435);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 436);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 437);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 438);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 439);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 440);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 441);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 442);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 443);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 444);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 445);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 446);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 447);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 448);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 449);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 450);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 451);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 452);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 453);
INSERT INTO value_value (parent_id, child_id) VALUES (362, 454);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 455);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 456);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 457);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 458);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 459);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 460);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 461);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 462);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 463);
INSERT INTO value_value (parent_id, child_id) VALUES (363, 464);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 465);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 466);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 467);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 468);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 469);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 470);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 471);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 472);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 473);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 474);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 475);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 476);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 477);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 478);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 479);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 480);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 482);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 483);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 484);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 485);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 486);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 487);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 488);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 489);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 490);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 491);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 492);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 493);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 494);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 495);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 496);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 497);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 498);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 499);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 500);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 501);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 502);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 503);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 504);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 505);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 506);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 507);
INSERT INTO value_value (parent_id, child_id) VALUES (364, 508);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 509);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 510);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 511);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 512);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 513);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 514);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 515);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 516);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 517);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 518);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 519);
INSERT INTO value_value (parent_id, child_id) VALUES (365, 520);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 521);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 522);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 523);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 524);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 525);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 527);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 528);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 529);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 530);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 531);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 532);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 533);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 534);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 535);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 536);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 537);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 538);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 539);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 540);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 541);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 542);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 543);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 544);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 545);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 546);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 547);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 548);
INSERT INTO value_value (parent_id, child_id) VALUES (366, 549);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 550);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 551);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 552);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 553);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 554);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 555);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 556);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 557);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 558);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 559);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 560);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 561);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 562);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 563);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 564);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 565);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 566);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 567);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 568);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 569);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 570);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 571);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 572);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 573);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 574);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 575);
INSERT INTO value_value (parent_id, child_id) VALUES (367, 576);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 577);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 578);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 579);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 580);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 581);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 582);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 583);
INSERT INTO value_value (parent_id, child_id) VALUES (368, 584);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 585);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 586);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 587);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 588);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 589);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 590);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 591);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 592);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 593);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 594);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 595);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 596);
INSERT INTO value_value (parent_id, child_id) VALUES (369, 597);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 598);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 599);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 600);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 601);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 602);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 603);
INSERT INTO value_value (parent_id, child_id) VALUES (370, 604);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 605);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 606);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 607);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 608);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 609);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 610);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 611);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 612);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 613);
INSERT INTO value_value (parent_id, child_id) VALUES (371, 614);
INSERT INTO value_value (parent_id, child_id) VALUES (372, 615);
INSERT INTO value_value (parent_id, child_id) VALUES (372, 616);
INSERT INTO value_value (parent_id, child_id) VALUES (372, 617);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 2);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 2);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 3);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 279);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 280);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 281);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 12);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 9);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 8);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 282);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 283);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 284);
INSERT INTO value_value (parent_id, child_id) VALUES (276, 285);
INSERT INTO value_value (parent_id, child_id) VALUES (286, 7);
INSERT INTO value_value (parent_id, child_id) VALUES (286, 6);
INSERT INTO value_value (parent_id, child_id) VALUES (286, 5);
INSERT INTO value_value (parent_id, child_id) VALUES (286, 269);
INSERT INTO value_value (parent_id, child_id) VALUES (275, 13);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 298);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 301);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 302);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 305);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 306);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 308);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 309);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 319);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 321);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 322);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 323);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 325);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 326);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 328);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 329);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 330);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 307);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 307);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 331);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 311);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 311);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 336);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 337);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 338);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 339);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 340);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 341);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 342);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 343);
INSERT INTO value_value (parent_id, child_id) VALUES (294, 344);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 345);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 346);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 347);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 348);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 349);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 300);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 300);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 350);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 351);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 352);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 312);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 312);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 353);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 354);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 355);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 356);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 357);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 299);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 313);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 314);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 320);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 315);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 318);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 316);
INSERT INTO value_value (parent_id, child_id) VALUES (292, 327);
INSERT INTO value_value (parent_id, child_id) VALUES (293, 333);
INSERT INTO value_value (parent_id, child_id) VALUES (296, 358);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 313);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 314);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 320);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 299);
INSERT INTO value_value (parent_id, child_id) VALUES (289, 317);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 303);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 303);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 297);
INSERT INTO value_value (parent_id, child_id) VALUES (295, 297);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 297);
INSERT INTO value_value (parent_id, child_id) VALUES (288, 310);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 310);
INSERT INTO value_value (parent_id, child_id) VALUES (293, 332);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 332);
INSERT INTO value_value (parent_id, child_id) VALUES (293, 335);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 335);
INSERT INTO value_value (parent_id, child_id) VALUES (293, 334);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 334);
INSERT INTO value_value (parent_id, child_id) VALUES (287, 304);
INSERT INTO value_value (parent_id, child_id) VALUES (290, 304);
INSERT INTO value_value (parent_id, child_id) VALUES (291, 324);

--
-- PostgreSQL database dump complete
--

ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1000;

--
-- PostgreSQL database dump complete
--

update property set mandatory=false where id not in (10,11);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Кокос','kokos',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Сметана','smetana',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Сгущенка','sguschenka',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Вафли','vafli',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Нуга','nuga',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Карамель','karamel',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Кофе','cofe',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Алкоголь','alkogol',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Желе','zhele',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Ягоды','yagody',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Мастика' ,'mastika',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Изюм','izum',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Сухофрукты','suhofrukty',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Кекс','keks',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем масляный' ,'krem_maslyany',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем сливочный' ,'krem_slivochny',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем заварной' ,'krem_zavarnoy',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем сметанный' ,'krem_smetanny',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем белковый','krem_belkovy',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем шоколадный' ,'krem_shokoladny',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Крем творожный','krem_tvorozhny',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Глазурь шоколадная' ,'',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Глазурь белковая' ,'',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Глазурь сахарная' ,'',true);
insert into property_value (id,property_id,name,alt_id,root_value) values (nextval('hibernate_sequence'),11,'Глазурь помадка','',true);