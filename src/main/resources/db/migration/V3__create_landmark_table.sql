CREATE TABLE landmark (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    description TEXT,
    location VARCHAR(255),
    mountain_id BIGINT NOT NULL,
    route_id BIGINT,
    CONSTRAINT fk_landmark_mountain FOREIGN KEY(mountain_id)
        REFERENCES mountain(id) ON DELETE CASCADE,
    CONSTRAINT fk_landmark_route FOREIGN KEY(route_id)
        REFERENCES hiking_route(id) ON DELETE SET NULL
);
