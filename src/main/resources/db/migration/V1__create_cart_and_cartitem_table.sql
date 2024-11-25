CREATE TABLE cartdb_dev.cart
(
    cart_id    VARCHAR(255) NOT NULL,
    user_id    VARCHAR(255) NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    CONSTRAINT pk_cart PRIMARY KEY (cart_id)
);

CREATE TABLE cartdb_dev.cart_item
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity   INT          NOT NULL,
    price DOUBLE NOT NULL,
    cart_id    VARCHAR(255) NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    CONSTRAINT pk_cartitem PRIMARY KEY (id)
);

ALTER TABLE cartdb_dev.cart_item
    ADD CONSTRAINT FK_CARTITEM_ON_CART FOREIGN KEY (cart_id) REFERENCES cartdb_dev.cart (cart_id);