CREATE TABLE category (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_ad DATETIME(6) NOT NULL,
    updated_ad DATETIME(6) NOT NULL,
    deleted_ad DATETIME(6) NULL
);