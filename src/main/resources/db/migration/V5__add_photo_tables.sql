CREATE TABLE mountain_photo (
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(512) NOT NULL,
    description TEXT,
    mountain_id BIGINT NOT NULL REFERENCES mountain(id) ON DELETE CASCADE
);

CREATE TABLE event_photo (
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(512) NOT NULL,
    description TEXT,
    event_id    BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE
);

CREATE TABLE challenge_photo (
    id           BIGSERIAL PRIMARY KEY,
    url          VARCHAR(512) NOT NULL,
    description  TEXT,
    challenge_id BIGINT NOT NULL REFERENCES challenge(id) ON DELETE CASCADE
);

CREATE TABLE hut_photo (
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(512) NOT NULL,
    description TEXT,
    hut_id      BIGINT NOT NULL REFERENCES hut(id) ON DELETE CASCADE
);
