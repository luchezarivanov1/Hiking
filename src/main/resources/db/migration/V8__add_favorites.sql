CREATE TABLE favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    entity_type VARCHAR(32) NOT NULL,
    entity_id BIGINT NOT NULL,
    UNIQUE (user_id, entity_type, entity_id)
);

CREATE INDEX idx_favorite_user_type ON favorite(user_id, entity_type);
