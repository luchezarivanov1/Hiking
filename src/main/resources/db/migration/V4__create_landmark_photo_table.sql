CREATE TABLE landmark_photo (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    description TEXT,
    landmark_id BIGINT NOT NULL,
    CONSTRAINT fk_photo_landmark FOREIGN KEY(landmark_id)
        REFERENCES landmark(id) ON DELETE CASCADE
);
