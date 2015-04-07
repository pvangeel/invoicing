# --- !Ups

CREATE TABLE Product (
  id          SERIAL,
  description VARCHAR(255) NOT NULL,
  price       NUMERIC      NOT NULL,
  vat         NUMERIC      NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE Customer (
  id         SERIAL,
  name       VARCHAR(255) NOT NULL,
  street     VARCHAR(255),
  number     VARCHAR(64),
  postalCode VARCHAR(64),
  city       VARCHAR(64),
  vat        VARCHAR(64),
  PRIMARY KEY (id)
);

CREATE TABLE Invoice (
  id            SERIAL,
  customerId    INTEGER REFERENCES Customer (id) NOT NULL,
  invoiceNumber VARCHAR(64)                      NOT NULL UNIQUE,
  date          DATE,
  PRIMARY KEY (id)
);

CREATE TABLE InvoiceLine (
  id        SERIAL,
  invoiceId INTEGER REFERENCES Invoice (id) NOT NULL,
  productId INTEGER REFERENCES Product (id) NOT NULL,
  quantity  INTEGER                         NOT NULL,
  price     NUMERIC                         NOT NULL,
  vat       NUMERIC                         NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE InvoiceLine;
DROP TABLE Invoice;
DROP TABLE Customer;
DROP TABLE Product;
