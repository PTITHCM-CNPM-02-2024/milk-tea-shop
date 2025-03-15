ALTER TABLE milk_tea_shop_prod.Account
    ADD token_version INT UNSIGNED DEFAULT 0 NOT NULL COMMENT 'Kiểm tra tính hợp lệ của token' AFTER last_login;