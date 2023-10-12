-- Создание таблицы Role (Роли)
CREATE TABLE Role (
                      role_id SERIAL PRIMARY KEY,
                      role_name VARCHAR(255) NOT NULL
);

-- Создание таблицы Users (Пользователи)
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       second_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role_id INT,
                       FOREIGN KEY (role_id) REFERENCES Role(role_id)
);

-- Создание таблицы Category (Категории товаров)
CREATE TABLE Category (
                          category_id SERIAL PRIMARY KEY,
                          category_name VARCHAR(255) NOT NULL,
                          parent_category_id INT,
                          FOREIGN KEY (parent_category_id) REFERENCES Category(category_id)
);

-- Создание таблицы Discount (Скидки)
CREATE TABLE Discount (
                          discount_id SERIAL PRIMARY KEY,
                          discount_percent DECIMAL(5, 2) NOT NULL
);

-- Создание таблицы Product (Товары)
CREATE TABLE Product (
                         product_id SERIAL PRIMARY KEY,
                         discount_id SERIAL,
                         product_name VARCHAR(255) NOT NULL,
                         description TEXT,
                         stock_quantity SERIAL NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         discount_price  DECIMAL(10, 2) NOT NULL,
                         image_url TEXT,
                         FOREIGN KEY (discount_id) REFERENCES Discount(discount_id)
);

-- Создание таблицы Product_Category (Связь между Товарами и Категориями)
CREATE TABLE Product_Category (
                                  product_category_id SERIAL PRIMARY KEY,
                                  product_id INT,
                                  category_id INT,
                                  FOREIGN KEY (product_id) REFERENCES Product(product_id),
                                  FOREIGN KEY (category_id) REFERENCES Category(category_id)
);


-- Создание таблицы Comment (Комментарии)
CREATE TABLE Comment (
                         comment_id SERIAL PRIMARY KEY,
                         user_id INT,
                         product_id INT,
                         text TEXT NOT NULL,
                         rating INT CHECK (rating >= 0 AND rating <= 5),
                         image_url TEXT,
                         FOREIGN KEY (user_id) REFERENCES Users(user_id),
                         FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

-- Создание таблицы Orders (Заказы)
CREATE TABLE Orders (
                        order_id SERIAL PRIMARY KEY,
                        user_id INT,
                        order_date DATE NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Создание таблицы Order_Item (Товары в заказе)
CREATE TABLE Order_Item (
                            order_item_id SERIAL PRIMARY KEY,
                            order_id INT,
                            product_id INT,
                            quantity INT NOT NULL,
                            FOREIGN KEY (order_id) REFERENCES Orders(order_id),
                            FOREIGN KEY (product_id) REFERENCES Product(product_id)
);