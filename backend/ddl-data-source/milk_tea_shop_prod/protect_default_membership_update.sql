create definer = root@localhost trigger milk_tea_shop_prod.protect_default_membership_update
    before update
    on milk_tea_shop_prod.membership_type
    for each row
begin
    -- missing source code
end;

