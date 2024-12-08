CREATE TABLE cartdb_dev.cart_metadata
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    device_type      VARCHAR(255) NULL,
    browser          VARCHAR(255) NULL,
    operating_system VARCHAR(255) NULL,
    ip_address       VARCHAR(255) NULL,
    additional_info  LONGTEXT NULL,
    CONSTRAINT pk_cartmetadata PRIMARY KEY (id)
);

ALTER TABLE cartdb_dev.cart
    ADD active BIT(1) NULL;

ALTER TABLE cartdb_dev.cart
    ADD metadata_id BIGINT NULL;

ALTER TABLE cartdb_dev.cart
    MODIFY active BIT (1) NOT NULL DEFAULT TRUE;

ALTER TABLE cartdb_dev.cart
    ADD CONSTRAINT uc_cart_metadata UNIQUE (metadata_id);

ALTER TABLE cartdb_dev.cart
    ADD CONSTRAINT FK_CART_ON_METADATA FOREIGN KEY (metadata_id) REFERENCES cartdb_dev.cart_metadata (id);