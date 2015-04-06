# --- !Ups

CREATE TABLE Product (
    id SERIAL,
    description varchar(255) NOT NULL,
    price varchar(255) NOT NULL,
    vat varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Product;