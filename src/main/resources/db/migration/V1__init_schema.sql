CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');

CREATE TABLE users (
    id                    BIGSERIAL PRIMARY KEY,
    username              VARCHAR(100) UNIQUE NOT NULL,
    email                 VARCHAR(150) UNIQUE NOT NULL,
    password              VARCHAR(255) NOT NULL,
    experience_level      VARCHAR(50),
    profile_image_url     TEXT,
    total_distance_km     DOUBLE PRECISION DEFAULT 0,
    total_hikes_completed INT              DEFAULT 0
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE user_friends (
    user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE mountain (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    region       VARCHAR(150),
    highest_peak DOUBLE PRECISION
);

CREATE TABLE hiking_route (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    distance_km  DOUBLE PRECISION,
    duration_min INT,
    difficulty   VARCHAR(50),
    description  TEXT,
    mountain_id  BIGINT REFERENCES mountain(id)
);

CREATE TABLE hut (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    address         VARCHAR(255),
    capacity        INT,
    open_year_round BOOLEAN,
    rating          DOUBLE PRECISION,
    mountain_id     BIGINT REFERENCES mountain(id),
    route_id        BIGINT REFERENCES hiking_route(id)
);

CREATE TABLE landmark (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(50),
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    description TEXT,
    mountain_id BIGINT NOT NULL REFERENCES mountain(id) ON DELETE CASCADE,
    route_id    BIGINT        REFERENCES hiking_route(id) ON DELETE SET NULL
);

CREATE TABLE landmark_photo (
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(512) NOT NULL,
    description TEXT,
    landmark_id BIGINT NOT NULL REFERENCES landmark(id) ON DELETE CASCADE
);

CREATE TABLE route_photo (
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(512) NOT NULL,
    description TEXT,
    route_id    BIGINT NOT NULL REFERENCES hiking_route(id) ON DELETE CASCADE
);

CREATE TABLE route_waypoint (
    id          BIGSERIAL PRIMARY KEY,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    description VARCHAR(500),
    order_index INT,
    route_id    BIGINT REFERENCES hiking_route(id)
);

CREATE TABLE review (
    id          BIGSERIAL PRIMARY KEY,
    comment     TEXT,
    rating      INT,
    user_id     BIGINT REFERENCES users(id),
    route_id    BIGINT REFERENCES hiking_route(id),
    hut_id      BIGINT REFERENCES hut(id),
    landmark_id BIGINT REFERENCES landmark(id)
);

CREATE TABLE event (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(150),
    description TEXT,
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    location    VARCHAR(255)
);

CREATE TABLE challenge (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150),
    description  TEXT,
    type         VARCHAR(50),
    target_count INT
);
