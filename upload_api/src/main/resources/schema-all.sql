DROP TABLE product IF EXISTS;

CREATE TABLE product  (
    product_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    product_name VARCHAR(255)
);