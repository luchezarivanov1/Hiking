ALTER TABLE review
    ADD COLUMN event_id BIGINT REFERENCES event(id);
