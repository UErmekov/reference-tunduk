CREATE TABLE refs
(
    id            VARCHAR(255) NOT NULL,
    ticket_id     VARCHAR(255) NOT NULL UNIQUE,
    owner         VARCHAR(255) NOT NULL,
    state         VARCHAR(255) NOT NULL,
    content       LONGBLOB,
    content_type  VARCHAR(255),
    price         DECIMAL(38, 2),
    size          BIGINT,
    duration      BIGINT,
    delivery_time DATETIME(6),
    updated_at    DATETIME(6),
    created_at    DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
