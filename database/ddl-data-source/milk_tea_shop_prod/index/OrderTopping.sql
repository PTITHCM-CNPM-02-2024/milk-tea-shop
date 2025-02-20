create index order_item_id
    on milk_tea_shop_prod.OrderTopping (order_product_id);

create index topping_id
    on milk_tea_shop_prod.OrderTopping (topping_id);

