CREATE TABLE route_photo (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    description TEXT,
    route_id BIGINT NOT NULL,
    CONSTRAINT fk_route_photo FOREIGN KEY(route_id)
        REFERENCES hiking_route(id) ON DELETE CASCADE
);
