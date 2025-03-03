ALTER TABLE milk_tea_shop_prod.Category
    ADD parent_category_id SMALLINT UNSIGNED NULL COMMENT 'Mã danh mục sản phẩm cha';

ALTER TABLE milk_tea_shop_prod.Category
    ADD CONSTRAINT Category_ibfk_1 FOREIGN KEY (parent_category_id) REFERENCES milk_tea_shop_prod.Category (category_id) ON DELETE NO ACTION;

CREATE INDEX Category_ibfk_1 ON milk_tea_shop_prod.Category (parent_category_id);