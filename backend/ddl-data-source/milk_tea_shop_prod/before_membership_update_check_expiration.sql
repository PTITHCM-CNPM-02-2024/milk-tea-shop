create definer = root@localhost trigger milk_tea_shop_prod.before_membership_update_check_expiration
    before update
    on milk_tea_shop_prod.membership_type
    for each row
begin
    -- missing source code
end;

