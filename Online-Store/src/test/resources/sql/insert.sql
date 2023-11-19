INSERT INTO Role (role_name) VALUES
                                 ('ADMIN'),
                                 ('USER');

INSERT INTO Users (first_name, second_name, email, password_hash, role_id) VALUES
                                                                               ('Artem', 'Lihachev', 'sofaross228@gmail.com', '$2a$10$FL10Q5wfqzeWKC9cV/q73uSMgsOgL3fOrtnLq4vmenVXQ7YQCVH4G', 1),
                                                                               ('Katerina', 'Katerina', 'Qwerty@gmail.com', '$2a$10$FL10Q5wfqzeWKC9cV/q73uSMgsOgL3fOrtnLq4vmenVXQ7YQCVH4G', 2),
                                                                               ('Bob', 'Johnson', 'bob.johnson@example.com', 'password', 2);

INSERT INTO Category (category_name, parent_category_id) VALUES
                                                             ('Смартфоны', NULL),
                                                             ('АудиоТехника', NULL),
                                                             ('Сопутствующие товары', 1),
                                                             ('Apple', 1),
                                                             ('Samsung', 1),
                                                             ('Чехлы', 3),
                                                             ('Наушники', 3),
                                                             ('Портативные колонки', 2),
                                                             ('Наушники', 2);

INSERT INTO Discount (discount_percent) VALUES
                                            (0.0),
                                            (10.00),
                                            (15.00),
                                            (20.00);

INSERT INTO Product (discount_id, product_name, description, stock_quantity, price, discount_price, image_url) VALUES
                                            (1, 'Смартфон Apple iPhone 14', 'У iPhone 14 Pro ',0, 89988.00, 89988.00, 'iphone14_image.jpg'),
                                            (1, 'Смартфон Apple iPhone 13', 'iPhone 13', 200, 77999.00, 77999.00, 'iphone13_image.jpg'),
                                            (2, 'Смартфон Samsung Galaxy S21',  'Смартфон Samsung Galaxy S21', 300, 45499.00, 40949.10, 'samsung_s12FE_image.jpg'),
                                            (2, 'Наушники Apple AirPods Pro',   'Наушники TWS Apple AirPods Pro', 0, 23699.00, 21329.10, 'airpods_pro_image.jpg'),
                                            (3, 'Чехол для Huawei P50','Чехол-книжка для HUAWEI P50', 100, 160.00, 136.00, 'huawei_p50_case_image.jpg'),
                                            (3, 'Смартфон Huawei P50', 'Смартфон HUAWEI P50', 100, 49299.00, 41904.15, 'huawei_p50_image.jpg'),
                                            (4, 'Умная колонка Яндекс Станция', 'Умная колонка Яндекс Станция 2',120, 17999.00, 14399.20, 'alica_image.jpg');

INSERT INTO Product_Category (product_id, category_id) VALUES
                                                           (1, 4),
                                                           (2, 4),
                                                           (3, 5),
                                                           (4, 7),
                                                           (4, 9),
                                                           (5, 8),
                                                           (6, 1),
                                                           (7, 8);


INSERT INTO Comment (user_id, product_id, text, rating, image_url) VALUES
                                                                       (1, 1, 'Отличный смартфон, работает быстро и камера супер!', 5, 'image.txt'),
                                                                       (2, 1, 'заблокирована еСим', 1, NULL),
                                                                       (3, 2, 'Смартфон понравился, особенно аккумулятор', 5, NULL),
                                                                       (1, 2, 'камера Звук!!!! очень порадовал ,быстродействие', 5, NULL),
                                                                       (2, 2, 'Отличное видео и фото', 5, NULL),
                                                                       (1, 4, 'Отличные наушники, звук чистый и громкий', 5, NULL),
                                                                       (3, 5, 'Чехол качественный, приятный на ощупь', 4, 'image.txt');


INSERT INTO Orders (user_id, order_date, total_price)
VALUES
    (1, '2023-10-10', 150000.00),
    (2, '2023-10-11', 85000.00),
    (2, '2023-10-12', 65000.00);

INSERT INTO Order_Item (order_id, product_id, quantity)
VALUES
    (1, 1, 2),
    (1, 3, 1);


