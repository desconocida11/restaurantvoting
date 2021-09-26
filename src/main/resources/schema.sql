DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;


CREATE TABLE users
(
    id               bigint default global_seq.nextval primary key,
    name             VARCHAR(255)  NOT NULL,
    email            VARCHAR(255)  NOT NULL,
    password         VARCHAR(255)  NOT NULL,
    registered       TIMESTAMP     DEFAULT now() NOT NULL,
    enabled          BOOLEAN   DEFAULT TRUE  NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id bigint NOT NULL,
    role    VARCHAR(255),
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurants
(
    id          bigint default global_seq.nextval primary key,
    name        VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX restaurants_name_unique_idx ON restaurants (name);

CREATE TABLE dishes
(
    id          bigint default global_seq.nextval primary key,
    name        VARCHAR(255) NOT NULL,
    price       INTEGER NOT NULL,
    day   DATE DEFAULT now() NOT NULL,
    restaurant_id bigint NOT NULL,
    CONSTRAINT name_day_restaurant_idx UNIQUE (day, restaurant_id, name),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE votes
(
    id   bigint default global_seq.nextval primary key,
    day  DATE DEFAULT now() NOT NULL,
    time TIME DEFAULT now() NOT NULL,
    user_id    bigint NOT NULL,
    restaurant_id    bigint NOT NULL,
    CONSTRAINT vote_per_day_idx UNIQUE (day, user_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE RESTRICT
);