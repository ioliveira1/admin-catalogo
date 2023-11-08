CREATE TABLE genres (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL
);

CREATE TABLE genres_categories (
    genre_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    CONSTRAINTS idx_genre_category UNIQUE (genre_id, category_id),
    CONSTRAINTS fk_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINTS fk_category_id FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
);