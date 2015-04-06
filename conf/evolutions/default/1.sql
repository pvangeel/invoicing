# --- !Ups

CREATE TABLE Product (
    id SERIAL,
    description varchar(255) NOT NULL,
    price NUMERIC NOT NULL,
    vat NUMERIC NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Product;