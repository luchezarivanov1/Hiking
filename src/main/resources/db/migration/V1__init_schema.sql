CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    experience_level VARCHAR(50),
    profile_image_url TEXT
);

CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE mountains (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    highest_peak VARCHAR(150),
    region VARCHAR(150)
);

CREATE TABLE huts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    altitude INT,
    capacity INT,
    food_available BOOLEAN,
    water_available BOOLEAN,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    mountain_id BIGINT REFERENCES mountains(id)
);

CREATE TABLE hiking_routes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    difficulty VARCHAR(50),
    distance_km DOUBLE PRECISION,
    elevation_gain INT,
    estimated_time INT,
    gpx_file_url TEXT,
    mountain_id BIGINT REFERENCES mountains(id)
);

CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES users(id),
    route_id BIGINT REFERENCES hiking_routes(id),
    hut_id BIGINT REFERENCES huts(id)
);

CREATE TABLE favorites (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    route_id BIGINT REFERENCES hiking_routes(id)
);

CREATE TABLE hiking_logs (
    id SERIAL PRIMARY KEY,
    date DATE,
    time_spent INT,
    notes TEXT,
    photos_url TEXT,
    user_id BIGINT REFERENCES users(id),
    route_id BIGINT REFERENCES hiking_routes(id)
);