INSERT INTO payments (id, image, method) VALUES (1, '/img/cash.png', 'Gotówka');
INSERT INTO payments (id, image, method) VALUES (2, '/img/payU.png', 'PayU');

INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (8, 'Napoje', null, null, 'Sprite®', 7.9);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (9, 'Napoje', null, null, 'Coca-Cola® Zero', 7.9);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (2, 'McWrapy i Sałatki', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/863138906', null, 'McZestaw McWrap® Chrupiący Klasyczny', 31.4);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (4, 'Burgery', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/1909301366', null, 'McZestaw Big Mac®', 31.6);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (3, 'Burgery', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/1162314903', null, 'Big Mac®', 22.3);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (1, 'McWrapy i Sałatki', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/-1466655237', null, 'McWrap® Chrupiący Klasyczny', 23.2);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (5, 'Burgery', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/220639894', null, 'WieśMac® Podwójny', 28.6);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (7, 'Napoje', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/ar_1:1,c_thumb,h_120,w_120/f_auto/q_auto/dpr_1.0/v1729850698/static-takeaway-com/images/restaurants/pl/QP7O33RN/products/-879071002', null, 'Coca-Cola®', 7.9);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (6, 'Napoje', null, null, 'Woda gazowana', 8.5);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (10, 'Przystawki', '', 'pieczywo, burrata, mortadela, bazylia', 'Bruschetta', 41);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (11, 'Przystawki', '', 'tuńczyk, mango, ogórek, mięta, piklowana cebula, pieczywo', 'Tartare di tonno', 56);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (12, 'Pizza', '', 'sos pomidorowy, mozzarella, szynka, pieczarki', 'Pizza Prosciutto e Funghi', 45.5);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (13, 'Pizza', '', 'ciasto, sos, ananas, ser, kurczak', 'Pizza Hawajska 40cm', 49);
INSERT INTO menus (id, category, image, ingredients, name, price) VALUES (14, 'Pieczywo', 'xd', 'chleb', 'Chleb', 10);

INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (1, 'Amerykańska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header/headers/header', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header_standard/logo_465x320', 'McDonald''s Tarnów, Krakowska');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (2, 'Burgery', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header/headers/header', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header_standard/logo_465x320', 'McDonald''s Tarnów, Jana Pawła');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (3, 'Fast Food', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header/headers/header', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1729851960/static-takeaway-com/images/chains/pl/mcdonalds_header_standard/logo_465x320', 'McDonald''s Tarnów, Szkotnik');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (4, 'Pizza', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732177560/static-takeaway-com/images/generic/heroes/1386/1386_pizza_59', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732179422/static-takeaway-com/images/restaurants/pl/RP7QQQRN/logo_465x320', 'Rodeo Restauracja');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (5, 'Włoska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732177656/static-takeaway-com/images/generic/heroes/21/21_italian_54', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732177656/static-takeaway-com/images/restaurants/pl/QO771N13/logo_465x320', 'NapOli Trattoria');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (6, 'Sushi', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732178554/static-takeaway-com/images/generic/heroes/51/51_sushi_60', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732178554/static-takeaway-com/images/restaurants/pl/Q1735N13/logo_465x320', 'Jani Sushi');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (9, 'Indyjska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732180877/static-takeaway-com/images/generic/heroes/1596/71_indian_51', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732180877/static-takeaway-com/images/restaurants/pl/QQ5POQ03/logo_465x320', 'Indie Kebab and Curry');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (10, 'Polska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732181145/static-takeaway-com/images/generic/heroes/1546/1546_veal_9', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732181145/static-takeaway-com/images/restaurants/pl/OPQ1PRPN/logo_465x320', 'Domowa Bistro');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (11, 'Tajska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732181414/static-takeaway-com/images/generic/heroes/61/61_thai_51', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732181414/static-takeaway-com/images/restaurants/pl/NNQ70O7/logo_465x320', 'ThaiCooking');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (7, 'Ciasto', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732179740/static-takeaway-com/images/generic/heroes/251/251_cake_7', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732179740/static-takeaway-com/images/chains/pl/ciacho_bez_cukru/logo_465x320', 'Ciacho Bez Cukru');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (8, 'Kuchnia Arabska', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1732180390/static-takeaway-com/images/generic/heroes/1606/531_kebab_15', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1732180390/static-takeaway-com/images/restaurants/pl/Q17QRORN/logo_465x320', 'Mustafa Kebab');
INSERT INTO restaurants (id, category, image_url, logo_url, name) VALUES (12, 'Pizza', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,w_1097,h_480/f_auto/q_auto/dpr_1.0/v1734525587/static-takeaway-com/images/generic/heroes/1386/1386_pizza_91', 'https://res.cloudinary.com/tkwy-prod-eu/image/upload/c_thumb,h_120,w_176/f_auto/q_auto/dpr_1.0/v1734525587/static-takeaway-com/images/restaurants/pl/0Q3OQQ11/logo_465x320', 'Restauracja Villa Toscana');

INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (1, 'Tarnów', '309', 49.9870677, 20.92365244458111, 'Krakowska', '33-100', 1);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (2, 'Tarnów', '26', 50.034368650000005, 21.0114193, 'aleja Jana Pawła II', '33-100', 2);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (3, 'Tarnów', '2E', 50.017735959506645, 20.976062956397396, 'Szkotnik', '33-100', 3);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (5, 'Tarnów', '42', 50.013433, 20.973208758439824, 'Pułaskiego', '33-100', 5);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (6, 'Tarnów', '42', 50.013976150000005, 20.978320694299676, 'Krasinskiego', '33-100', 6);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (7, 'Tarnów', '36', 50.0133199, 20.9908261, 'Wałowa', '33-100', 7);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (8, 'Tarnów', '40B', 50.01152083011571, 20.97956506389516, 'Mościckiego', '33-100', 8);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (9, 'Tarnów', '3', 50.011522400000004, 20.98279884642857, 'Nowy Świat', '33-100', 9);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (10, 'Tarnów', '30', 50.02387875, 20.988074833564724, 'Józefa Piłsudskiego', '33-100', 10);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (11, 'Tarnów', '46', 50.0144227, 21.0013931430518, 'Lwowska', '33-100', 11);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (12, 'Zakopane', '10', 49.296559650000006, 19.959398831789837, 'Sienkiewicza', '34-500', 12);
INSERT INTO restaurant_addresses (id, city, flat_number, latitude, longitude, street, zip_code, restaurant_id) VALUES (4, 'Tarnów', '2', 0, 0, 'Kasprowicza', '33-100', 4);

INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (2, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (2, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (1, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (1, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (3, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (3, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (5, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (6, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (7, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (8, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (9, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (10, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (11, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (5, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (6, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (7, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (8, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (9, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (10, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (11, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (12, 2);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (12, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (4, 1);
INSERT INTO restaurant_payments (restaurant_id, payment_id) VALUES (4, 2);

INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (1, 65, 40, 2, 30, 0, 1);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (2, 65, 40, 7, 30, 15, 2);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (3, 65, 40, 7, 30, 0, 3);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (4, 70, 35, 7, 60, 20, 4);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (5, 60, 30, 0, 60, 15, 5);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (6, 50, 25, 6, 60, 20, 6);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (7, 45, 20, 10, 50, 15, 7);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (8, 50, 30, 6, 40, 15, 8);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (9, 45, 20, 5, 45, 15, 9);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (10, 75, 50, 5, 25, 15, 10);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (11, 70, 50, 6, 45, 20, 11);
INSERT INTO deliveries (id, delivery_max_time, delivery_min_time, delivery_price, minimum_price, pickup_time, restaurant_id) VALUES (12, 60, 20, 4, 35, 15, 12);


INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (1, '22:45', 0, '11:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (2, '22:45', 1, '11:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (3, '22:45', 2, '11:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (4, '22:45', 5, '11:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (5, '22:45', 6, '11:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (6, '22:45', 4, '12:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (7, '22:45', 3, '12:45', 1);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (8, '22:45', 4, '11:45', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (11, '22:45', 0, '11:45', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (12, '22:45', 1, '11:45', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (13, '22:45', 2, '11:45', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (14, '22:45', 3, '11:45', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (15, '22:45', 2, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (16, '22:45', 3, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (17, '22:45', 4, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (18, '22:45', 5, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (19, '22:45', 6, '11:15', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (20, '22:45', 0, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (21, '22:45', 1, '11:45', 3);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (22, '20:00', 3, '11:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (23, '20:00', 4, '11:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (24, '20:00', 5, '11:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (27, '20:00', 1, '11:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (28, '20:00', 2, '11:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (29, '20:30', 2, '12:00', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (30, '20:30', 4, '12:00', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (31, 'null', 5, 'null', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (32, 'null', 6, 'null', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (33, '20:30', 0, '12:00', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (34, '20:30', 1, '12:00', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (35, '20:30', 3, '12:00', 5);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (36, '21:00', 6, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (37, '21:00', 0, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (38, '21:00', 1, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (39, '21:00', 2, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (40, '21:00', 3, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (41, '21:00', 4, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (42, '21:00', 5, '12:00', 6);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (43, '18:00', 3, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (44, '18:00', 4, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (45, '18:00', 5, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (46, '18:00', 6, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (47, '18:00', 2, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (48, '18:00', 1, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (49, '18:00', 0, '10:00', 7);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (50, '21:00', 3, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (51, '21:00', 4, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (52, '21:00', 5, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (53, '21:00', 6, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (54, '21:00', 0, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (55, '21:00', 1, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (56, '21:00', 2, '13:00', 8);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (57, '22:30', 3, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (58, '22:30', 4, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (59, '22:30', 5, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (60, '22:30', 6, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (61, '22:30', 0, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (62, '22:30', 1, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (63, '22:30', 2, '10:00', 9);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (64, '17:30', 3, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (65, '17:30', 4, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (66, '17:30', 5, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (67, '17:30', 6, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (68, '17:30', 0, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (69, '17:30', 1, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (70, '17:30', 2, '10:00', 10);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (71, '20:30', 3, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (72, '20:30', 4, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (73, '20:30', 5, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (74, '20:30', 6, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (75, '20:30', 0, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (76, '20:30', 1, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (77, '20:30', 2, '11:00', 11);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (9, '22:45', 5, '0:15', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (10, '22:45', 6, '00:15', 2);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (78, '22:00', 1, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (25, '23:00', 6, '14:30', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (26, '22:00', 0, '14:00', 4);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (79, '22:00', 2, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (80, '22:00', 3, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (81, '22:00', 4, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (82, '22:00', 5, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (83, '23:00', 6, '13:00', 12);
INSERT INTO delivery_hours (id, close_time, day_of_week, open_time, restaurant_id) VALUES (84, '23:00', 0, '13:00', 12);


INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 3);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 7);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 9);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 1);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 4);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 2);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 8);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 5);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (2, 6);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 3);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 7);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 9);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 1);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 4);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 2);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 8);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 5);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (1, 6);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 3);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 7);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 9);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 1);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 4);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 2);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 8);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 5);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (3, 6);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (12, 10);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (12, 11);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (12, 12);
INSERT INTO restaurant_menus (restaurant_id, menu_id) VALUES (4, 13);


INSERT INTO additives (id, name, price, value) VALUES (1, 'Rozmiar', 0, 'McZestaw');
INSERT INTO additives (id, name, price, value) VALUES (2, 'Rozmiar', 3, 'McZestaw powiększony');
INSERT INTO additives (id, name, price, value) VALUES (3, 'Dodatek', 0, 'Frytki');
INSERT INTO additives (id, name, price, value) VALUES (4, 'Dodatek', 0, 'Sałatka');
INSERT INTO additives (id, name, price, value) VALUES (5, 'Napój', 0, 'Sprite®');
INSERT INTO additives (id, name, price, value) VALUES (6, 'Napój', 0, 'Woda gazowana');
INSERT INTO additives (id, name, price, value) VALUES (7, 'Napój', 0, 'Coca-Cola® Zero');
INSERT INTO additives (id, name, price, value) VALUES (8, 'Napój', 0, 'Sprite®');
INSERT INTO additives (id, name, price, value) VALUES (9, 'Napój', 0, 'Kawa');
INSERT INTO additives (id, name, price, value) VALUES (10, 'Napój', 0, 'Herbata');
INSERT INTO additives (id, name, price, value) VALUES (11, 'Napój', 0, 'Cola');
INSERT INTO additives (id, name, price, value) VALUES (12, 'Napój', 0, 'Sprite');

INSERT INTO menu_additives (additive_id, menu_id) VALUES (1, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (1, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (2, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (2, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (3, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (3, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (4, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (4, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (5, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (5, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (6, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (6, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (7, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (7, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (8, 2);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (8, 4);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (9, 12);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (10, 12);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (11, 12);
INSERT INTO menu_additives (additive_id, menu_id) VALUES (12, 12);

INSERT INTO address_suggestions (id, lat, lon, name) VALUES (1, 49.9892392, 20.9011874, 'Sportowa 3, Zbylitowska Góra');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (2, 50.0111345, 20.982408484206346, 'Krakowska 13, Tarnów');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (3, 49.8564352, 20.8086652, '32-840 Zakliczyn');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (4, 50.0529334, 19.948661, 'Starowiślna 69, 31-035 Kraków');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (5, 50.05188608333333, 19.949849708333335, '31-035');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (6, 49.85496906411742, 20.804921425415863, 'Spokojna 10, Zakliczyn');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (7, 49.9721883, 20.6167351, 'Ludwika Solskiego 14a, 32-800 Brzesko');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (8, 49.296559650000006, 19.959398831789837, 'Sienkiewicza 10, 34-500 Zakopane');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (9, 49.2757934, 19.969281065702543, 'Zakopane');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (10, 49.9678396, 20.6068496, 'Brzesko');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (11, 49.971342250000006, 20.593644001225808, 'Leśna 2, 32-800 Brzesko');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (12, 50.02233082291197, 20.975425131903688, '33-100');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (13, 52.2337172, 21.071432235636493, 'Warszawa');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (14, 54.5164982, 18.5402738, 'Gdynia');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (15, 52.4082663, 16.9335199, 'Poznań');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (16, 51.1089776, 17.0326689, 'Wrocław');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (17, 52.52465015, 17.598152438162245, 'Gniezno');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (18, 50.2712401, 19.21556284229424, 'Sosnowiec');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (19, 48.8588897, 2.3200410217200766, 'Paris');
INSERT INTO address_suggestions (id, lat, lon, name) VALUES (20, 49.85496906411742, 20.804921425415863, 'Spokojna 10, Zakliczyn 32-840');


SELECT setval(
               pg_get_serial_sequence('restaurants', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM restaurants)
       );

SELECT setval(
               pg_get_serial_sequence('payments', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM payments)
       );

SELECT setval(
               pg_get_serial_sequence('menus', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM menus)
       );

SELECT setval(
               pg_get_serial_sequence('additives', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM additives)
       );

SELECT setval(
               pg_get_serial_sequence('deliveries', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM deliveries)
       );

SELECT setval(
               pg_get_serial_sequence('delivery_hours', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM delivery_hours)
       );

SELECT setval(
               pg_get_serial_sequence('restaurant_addresses', 'id'),
               (SELECT COALESCE(MAX(id),0) FROM restaurant_addresses)
       );

SELECT setval(
                pg_get_serial_sequence('address_suggestions', 'id'),
                (SELECT COALESCE(MAX(id),0) FROM address_suggestions)
       );
