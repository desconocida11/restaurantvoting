INSERT INTO users (id, name, email, password)
VALUES (1, 'User', 'user@yandex.ru', '{noop}password'),
       (2, 'Admin', 'admin@gmail.com', '{noop}admin'),
       (3, 'AdminOnly', 'admin_only@gmail.com', '{noop}admin');;

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('ADMIN', 3);

INSERT INTO restaurants (id, name)
VALUES (4, 'Metropol'),
       (5, 'Pushkin'),
       (6, 'NapoliPizza');

INSERT INTO dishes (id, name, price, restaurant_id)
VALUES (7, 'Smashed potatoes with steak', 12000, 4),
       (8, 'Cheesecake', 5000, 4),
       (9, 'Borsch with bread', 8000, 5),
       (10, 'Pumpkin Cake', 4000, 5),
       (11, 'Chicken Curry', 6000, 5),
       (12, 'Beef with veggies', 10000, 5),
       (13, 'Grilled vegetables', 7000, 5),
       (14, 'Spinach mousse', 3000, 6),
       (15, 'Milkshake', 200, 6),
       (16, 'Pancakes', 400, 6);

INSERT INTO dishes (id, name, price, restaurant_id, day)
values (17, 'Old Dish Cheese', 400, 6, '2021-08-20'),
       (18, 'Old Dish Smoothy', 300, 6, '2021-08-20'),
       (19, 'Old Dish Vegan Curry', 700, 5, '2021-08-20');

INSERT INTO votes (id, time, user_id, restaurant_id)
VALUES (20, '00:10:00', 2, 4);

INSERT INTO votes (id, day, time, user_id, restaurant_id)
VALUES (21, '2021-08-20', '09:00:00', 1, 5),
       (22, '2021-08-20', '10:00:00', 2, 4);

INSERT INTO restaurants (id, name)
VALUES (23, 'Dummy Restaurant');
