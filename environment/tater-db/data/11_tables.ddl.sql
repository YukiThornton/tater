\c tater tater

CREATE SCHEMA movie;

CREATE TABLE movie.user (
    id TEXT NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE movie.viewing_history (
    id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    PRIMARY KEY(id, user_id),
    FOREIGN KEY(user_id) REFERENCES movie.user(id)
);
