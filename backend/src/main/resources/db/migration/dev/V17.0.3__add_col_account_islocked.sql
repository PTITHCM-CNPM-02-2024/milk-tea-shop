ALTER TABLE milk_tea_shop_prod.Account
    ADD is_locked TINYINT(1) DEFAULT 0 NOT NULL COMMENT 'Tài khoản có bị khóa hay không (Có: 1, Không:0)' AFTER is_active;

ALTER TABLE milk_tea_shop_prod.Account
    MODIFY role_id TINYINT UNSIGNED NOT NULL;