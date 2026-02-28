CREATE TABLE user_challenges (
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id BIGINT NOT NULL REFERENCES challenge(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, challenge_id)
);

CREATE TABLE event_participants (
    event_id BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);
