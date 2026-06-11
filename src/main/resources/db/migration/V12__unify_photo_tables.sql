-- Unify the six per-entity photo tables into a single polymorphic `photo` table
-- (mirrors the `review` table's nullable-FK design).

CREATE TABLE photo (
    id           BIGSERIAL PRIMARY KEY,
    url          VARCHAR(512) NOT NULL,
    description  TEXT,
    route_id     BIGINT REFERENCES hiking_route(id) ON DELETE CASCADE,
    hut_id       BIGINT REFERENCES hut(id)          ON DELETE CASCADE,
    landmark_id  BIGINT REFERENCES landmark(id)     ON DELETE CASCADE,
    event_id     BIGINT REFERENCES event(id)        ON DELETE CASCADE,
    challenge_id BIGINT REFERENCES challenge(id)    ON DELETE CASCADE,
    mountain_id  BIGINT REFERENCES mountain(id)     ON DELETE CASCADE
);

CREATE INDEX idx_photo_route_id     ON photo(route_id);
CREATE INDEX idx_photo_hut_id       ON photo(hut_id);
CREATE INDEX idx_photo_landmark_id  ON photo(landmark_id);
CREATE INDEX idx_photo_event_id     ON photo(event_id);
CREATE INDEX idx_photo_challenge_id ON photo(challenge_id);
CREATE INDEX idx_photo_mountain_id  ON photo(mountain_id);

-- Backfill existing photos. IDs are reassigned; stored files and the frontend
-- reference photos by URL, not by id, so this is safe.
INSERT INTO photo (url, description, route_id)     SELECT url, description, route_id     FROM route_photo;
INSERT INTO photo (url, description, hut_id)       SELECT url, description, hut_id       FROM hut_photo;
INSERT INTO photo (url, description, landmark_id)  SELECT url, description, landmark_id  FROM landmark_photo;
INSERT INTO photo (url, description, event_id)     SELECT url, description, event_id     FROM event_photo;
INSERT INTO photo (url, description, challenge_id) SELECT url, description, challenge_id FROM challenge_photo;
INSERT INTO photo (url, description, mountain_id)  SELECT url, description, mountain_id  FROM mountain_photo;

DROP TABLE route_photo;
DROP TABLE hut_photo;
DROP TABLE landmark_photo;
DROP TABLE event_photo;
DROP TABLE challenge_photo;
DROP TABLE mountain_photo;
