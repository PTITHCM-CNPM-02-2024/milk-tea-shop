create table if not exists Area
(
    area_id     int auto_increment comment 'Mã khu vực'
        primary key,
    name        char(3)                              not null comment 'Tên khu vực',
    description varchar(255)                         null comment 'Mô tả',
    max_tables  int                                  not null comment 'Số bàn tối đa',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái hoạt động (1: Có, 0: Không)',
    created_at  datetime   default CURRENT_TIMESTAMP null,
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Area_pk
        unique (name)
);

create table if not exists Category
(
    category_id int auto_increment comment 'Mã danh mục'
        primary key,
    name        varchar(100)                       not null comment 'Tên danh mục',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Category_pk
        unique (name)
);

create table if not exists Coupon
(
    coupon_id   int auto_increment comment 'Mã coupon'
        primary key,
    coupon      varchar(15)                          not null comment 'Mã giảm giá',
    description text                                 null comment 'Mô tả',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at  datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint coupon
        unique (coupon)
);

create table if not exists DiscountType
(
    discount_type_id int auto_increment comment 'Mã loại giảm giá'
        primary key,
    promotion_name   varchar(100)                       not null comment 'Tên loại giảm giá',
    description      text                               null comment 'Mô tả',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountType_pk
        unique (promotion_name)
);

create table if not exists DiscountDetail
(
    discount_details_id     int auto_increment comment 'Mã chi tiết giảm giá'
        primary key,
    discount_type_id        int                                  null comment 'Mã loại giảm giá',
    apply_type              enum ('BEST', 'COMBINE')             not null comment 'Kiểu áp dụng (BEST, COMBINE)',
    discount_value          decimal(10, 2)                       not null comment 'Giá trị giảm giá',
    max_discount_amount     decimal(10, 2)                       null comment 'Số tiền giảm giá tối đa',
    is_allowed_reedem_point tinyint(1) default 0                 null comment 'Cho phép đổi điểm (1: Có, 0: Không)',
    created_at              datetime   default CURRENT_TIMESTAMP null,
    updated_at              datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountDetail_ibfk_1
        foreign key (discount_type_id) references DiscountType (discount_type_id)
);

create table if not exists CategoryDiscount
(
    category_discount_id  int auto_increment comment 'Mã giảm giá theo danh mục'
        primary key,
    category_id           int                                  null comment 'Mã danh mục',
    discount_details_id   int                                  null comment 'Mã chi tiết giảm giá',
    min_prod_nums         int                                  not null comment 'Số lượng sản phẩm tối thiểu',
    valid_from            datetime                             not null comment 'Ngày bắt đầu hiệu lực',
    valid_until           datetime                             not null comment 'Ngày kết thúc hiệu lực',
    current_uses          int        default 0                 null comment 'Số lần đã sử dụng',
    max_uses              int                                  null comment 'Số lần sử dụng tối đa',
    max_uses_per_customer int                                  null comment 'Số lần sử dụng tối đa cho mỗi khách hàng',
    is_active             tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at            datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at            datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint CategoryDiscount_ibfk_1
        foreign key (category_id) references Category (category_id),
    constraint CategoryDiscount_ibfk_2
        foreign key (discount_details_id) references DiscountDetail (discount_details_id)
);

create index category_id
    on CategoryDiscount (category_id);

create index discount_details_id
    on CategoryDiscount (discount_details_id);

create index discount_type_id
    on DiscountDetail (discount_type_id);

create table if not exists ExpenseCategory
(
    expense_category_id int auto_increment comment 'Mã loại chi phí'
        primary key,
    name                varchar(50)                        not null comment 'Tên loại chi phí (ví dụ: Nguyên liệu, Tiền điện, Lương nhân viên)',
    description         text                               null comment 'Mô tả',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ExpenseCategory_pk
        unique (name)
);

create table if not exists IncomeCategory
(
    income_category_id int auto_increment comment 'Mã loại thu nhập'
        primary key,
    name               varchar(50)                        not null comment 'Tên loại thu nhập (ví dụ: Bán hàng, Hoàn tiền)',
    description        text                               null comment 'Mô tả',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint IncomeCategory_pk
        unique (name)
);

create table if not exists Material
(
    material_id  int auto_increment comment 'Mã nguyên vật liệu'
        primary key,
    name         varchar(100)                       not null comment 'Tên nguyên vật liệu',
    description  text                               null comment 'Mô tả',
    unit         varchar(20)                        not null comment 'Đơn vị tính (e.g., kg, lít, cái)',
    min_quantity decimal(10, 2)                     not null comment 'Số lượng tối thiểu',
    max_quantity decimal(10, 2)                     not null comment 'Số lượng tối đa',
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Material_pk
        unique (name)
);

create table if not exists MembershipType
(
    membership_type_id int auto_increment comment 'Mã loại thành viên'
        primary key,
    type               enum ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') not null comment 'Loại thành viên',
    discount_value     int                                           not null comment 'Giá trị giảm giá',
    discount_unit      enum ('PERCENT', 'FIXED')                     not null comment 'Đơn vị giảm giá (PERCENT, FIXED)',
    required_point     int                                           not null comment 'Điểm yêu cầu',
    description        varchar(255)                                  null comment 'Mô tả',
    valid_until        datetime                                      null comment 'Ngày hết hạn',
    is_active          tinyint(1) default 1                          null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at         datetime   default CURRENT_TIMESTAMP          null,
    updated_at         datetime   default CURRENT_TIMESTAMP          null on update CURRENT_TIMESTAMP,
    constraint MembershipType_pk
        unique (type)
);

create table if not exists PaymentMethod
(
    payment_method_id   int auto_increment comment 'Mã phương thức thanh toán'
        primary key,
    payment_name        varchar(50)                        not null comment 'Tên phương thức thanh toán',
    payment_description varchar(255)                       null comment 'Mô tả phương thức thanh toán',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint PaymentMethod_pk
        unique (payment_name)
);

create table if not exists Product
(
    product_id   int auto_increment comment 'Mã sản phẩm'
        primary key,
    category_id  int                                  null comment 'Mã danh mục',
    name         varchar(100)                         not null comment 'Tên sản phẩm',
    description  text                                 null comment 'Mô tả',
    is_available tinyint(1) default 1                 null comment 'Sản phẩm có sẵn (1: Có, 0: Không)',
    is_signature tinyint(1) default 0                 null comment 'Sản phẩm đặc trưng (1: Có, 0: Không)',
    image        mediumblob                           null comment 'Hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint Product_ibfk_1
        foreign key (category_id) references Category (category_id)
);

create index category_id
    on Product (category_id);

create table if not exists ProductSize
(
    size_id     int auto_increment comment 'Mã kích thước'
        primary key,
    name        varchar(10)                        not null comment 'Tên kích thước (ví dụ: S, M, L)',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductSize_pk
        unique (name)
);

create table if not exists ProductPrice
(
    product_price_id int auto_increment comment 'Mã giá sản phẩm'
        primary key,
    product_id       int                                null comment 'Mã sản phẩm',
    size_id          int                                null comment 'Mã kích thước',
    price            decimal(10, 2)                     not null comment 'Giá',
    created_at       datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint ProductPrice_pk
        unique (product_id, size_id),
    constraint ProductPrice_ibfk_1
        foreign key (product_id) references Product (product_id),
    constraint ProductPrice_ibfk_2
        foreign key (size_id) references ProductSize (size_id)
);

create index product_id
    on ProductPrice (product_id);

create index size_id
    on ProductPrice (size_id);

create table if not exists ProductPriceDiscount
(
    product_price_discount_id int auto_increment comment 'Mã giảm giá theo giá sản phẩm'
        primary key,
    product_price_id          int                                  null comment 'Mã giá sản phẩm',
    discount_details_id       int                                  null comment 'Mã chi tiết giảm giá',
    min_required_prod         int                                  not null comment 'Số lượng sản phẩm tối thiểu',
    valid_from                datetime                             not null comment 'Ngày bắt đầu hiệu lực',
    valid_until               datetime                             not null comment 'Ngày kết thúc hiệu lực',
    current_uses              int        default 0                 null comment 'Số lần đã sử dụng',
    max_uses                  int                                  null comment 'Số lần sử dụng tối đa',
    max_uses_per_customer     int                                  null comment 'Số lần sử dụng tối đa cho mỗi khách hàng',
    is_active                 tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at                datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at                datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductPriceDiscount_ibfk_1
        foreign key (product_price_id) references ProductPrice (product_price_id),
    constraint ProductPriceDiscount_ibfk_2
        foreign key (discount_details_id) references DiscountDetail (discount_details_id)
);

create index discount_details_id
    on ProductPriceDiscount (discount_details_id);

create index product_price_id
    on ProductPriceDiscount (product_price_id);

create table if not exists Role
(
    role_id    int auto_increment comment 'Mã vai trò'
        primary key,
    name       varchar(50)                        not null comment 'Tên vai trò (ví dụ: admin, staff, customer)',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Role_pk
        unique (name)
);

create table if not exists Account
(
    account_id    int auto_increment comment 'Mã tài khoản'
        primary key,
    role_id       int                                  null comment 'Mã vai trò',
    username      varchar(50)                          not null comment 'Tên đăng nhập',
    password_hash varchar(255)                         not null comment 'Mật khẩu đã mã hóa',
    is_active     tinyint(1) default 1                 null comment 'Tài khoản hoạt động (1: Có, 0: Không)',
    last_login    timestamp                            null comment 'Lần đăng nhập cuối',
    created_at    timestamp  default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at    timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint username
        unique (username),
    constraint Account_ibfk_1
        foreign key (role_id) references Role (role_id)
);

create index role_id
    on Account (role_id);

create table if not exists Customer
(
    customer_id        int auto_increment comment 'Mã khách hàng'
        primary key,
    membership_type_id int                                 null comment 'Mã loại thành viên',
    account_id         int                                 null comment 'Mã tài khoản',
    last_name          varchar(35)                         not null comment 'Họ',
    first_name         varchar(25)                         not null comment 'Tên',
    phone              varchar(15)                         not null comment 'Số điện thoại',
    email              varchar(100)                        not null comment 'Email',
    current_points     int       default 0                 null comment 'Điểm hiện tại',
    sex                enum ('MALE', 'FEMALE', 'OTHER')    null comment 'Giới tính',
    created_at         timestamp default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at         timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Ngày cập nhật',
    constraint email
        unique (email),
    constraint phone
        unique (phone),
    constraint Customer_ibfk_1
        foreign key (membership_type_id) references MembershipType (membership_type_id),
    constraint Customer_ibfk_2
        foreign key (account_id) references Account (account_id)
);

create index account_id
    on Customer (account_id);

create index membership_type_id
    on Customer (membership_type_id);

create table if not exists Employee
(
    employee_id int auto_increment comment 'Mã nhân viên'
        primary key,
    account_id  int                                null comment 'Mã tài khoản',
    position    varchar(50)                        not null comment 'Chức vụ',
    last_name   varchar(35)                        not null comment 'Họ',
    first_name  varchar(25)                        not null comment 'Tên',
    sex         enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone       varchar(15)                        not null comment 'Số điện thoại',
    email       varchar(100)                       not null comment 'Email',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint account_id
        unique (account_id),
    constraint Employee_ibfk_1
        foreign key (account_id) references Account (account_id)
);

create table if not exists Expense
(
    expense_id          int auto_increment comment 'Mã chi phí'
        primary key,
    account_id          int                                null comment 'Mã tài khoản',
    expense_category_id int                                null comment 'Mã loại chi phí',
    amount              decimal(10, 2)                     not null comment 'Số tiền',
    transaction_date    datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    description         text                               null comment 'Mô tả',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Expense_ibfk_1
        foreign key (account_id) references Account (account_id),
    constraint Expense_ibfk_2
        foreign key (expense_category_id) references ExpenseCategory (expense_category_id)
);

create index account_id
    on Expense (account_id);

create index expense_category_id
    on Expense (expense_category_id);

create table if not exists Income
(
    income_id          int auto_increment comment 'Mã thu nhập'
        primary key,
    account_id         int                                null comment 'Mã tài khoản',
    income_category_id int                                null comment 'Mã loại thu nhập',
    amount             decimal(10, 2)                     not null comment 'Số tiền',
    transaction_date   datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    description        text                               null comment 'Mô tả',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Income_ibfk_1
        foreign key (account_id) references Account (account_id),
    constraint Income_ibfk_2
        foreign key (income_category_id) references IncomeCategory (income_category_id)
);

create index account_id
    on Income (account_id);

create index income_category_id
    on Income (income_category_id);

create table if not exists Manager
(
    manager_id int auto_increment comment 'Mã quản lý'
        primary key,
    account_id int                                null comment 'Mã tài khoản',
    last_name  varchar(35)                        not null comment 'Họ',
    first_name varchar(25)                        not null comment 'Tên',
    sex        enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone      varchar(15)                        not null comment 'Số điện thoại',
    email      varchar(100)                       not null comment 'Email',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint account_id
        unique (account_id),
    constraint Manager_ibfk_1
        foreign key (account_id) references Account (account_id)
);

create table if not exists RewardPointLog
(
    reward_point_log_id int auto_increment comment 'Mã lịch sử điểm thưởng'
        primary key,
    customer_id         int                                null comment 'Mã khách hàng',
    reward_point        int                                not null comment 'Số điểm thưởng',
    operation_type      enum ('EARN', 'REDEEM', 'ADJUST')  not null comment 'Loại thao tác (EARN, REDEEM, ADJUST)',
    created_at          datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint RewardPointLog_ibfk_1
        foreign key (customer_id) references Customer (customer_id)
);

create table if not exists ServiceTable
(
    table_id     int auto_increment comment 'Mã bàn'
        primary key,
    area_id      int                                  null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_available tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ServiceTable_pk
        unique (area_id, table_number),
    constraint ServiceTable_ibfk_1
        foreign key (area_id) references Area (area_id)
);

create table if not exists `Order`
(
    order_id        int auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id     int                                      null comment 'Mã khách hàng',
    employee_id     int                                      null comment 'Mã nhân viên',
    table_id        int                                      null comment 'Mã bàn',
    payment_id      int                                      null comment 'Mã thanh toán',
    order_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian đặt hàng',
    total_amount    decimal(10, 2)                           not null comment 'Tổng tiền',
    discount_amount decimal(10, 2) default 0.00              null comment 'Số tiền giảm giá',
    final_amount    decimal(10, 2)                           not null comment 'Thành tiền',
    notes           text                                     null comment 'Ghi chú',
    customize_note  text                                     null comment 'Ghi chú tùy chỉnh',
    created_at      datetime       default CURRENT_TIMESTAMP null,
    updated_at      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Order_ibfk_1
        foreign key (customer_id) references Customer (customer_id),
    constraint Order_ibfk_2
        foreign key (employee_id) references Employee (employee_id),
    constraint Order_ibfk_3
        foreign key (table_id) references ServiceTable (table_id)
);

create index employee_id
    on `Order` (employee_id);

create index table_id
    on `Order` (table_id);

create table if not exists OrderProduct
(
    order_product_id int auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id         int                                null comment 'Mã đơn hàng',
    product_price_id int                                null comment 'Mã giá sản phẩm',
    quantity         int                                not null comment 'Số lượng',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderProduct_pk
        unique (order_id, product_price_id),
    constraint OrderProduct_ibfk_1
        foreign key (order_id) references `Order` (order_id),
    constraint OrderProduct_ibfk_2
        foreign key (product_price_id) references ProductPrice (product_price_id)
);

create index order_id
    on OrderProduct (order_id);

create index product_price_id
    on OrderProduct (product_price_id);

create table if not exists OrderTable
(
    order_table_id int auto_increment comment 'Mã đơn hàng và bàn'
        primary key,
    order_id       int                                not null,
    table_id       int                                not null,
    check_in       datetime                           not null comment 'Thời gian vào bàn',
    check_out      datetime                           null comment 'Thời gian rời bàn',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderTable_pk
        unique (order_id, table_id),
    constraint OrderTable_ibfk_1
        foreign key (order_id) references `Order` (order_id),
    constraint OrderTable_ibfk_2
        foreign key (table_id) references ServiceTable (table_id)
);

create index order_id
    on OrderTable (order_id);

create index table_id
    on OrderTable (table_id);

create table if not exists Payment
(
    payment_id        int auto_increment comment 'Mã thanh toán'
        primary key,
    order_id          int                                      null comment 'Mã đơn hàng',
    payment_method_id int                                      null comment 'Mã phương thức thanh toán',
    amount_paid       decimal(10, 2)                           not null comment 'Số tiền đã trả',
    change_amount     decimal(10, 2) default 0.00              null comment 'Tiền thừa',
    payment_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian thanh toán',
    created_at        datetime       default CURRENT_TIMESTAMP null,
    updated_at        datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint order_id
        unique (order_id),
    constraint Payment_ibfk_1
        foreign key (order_id) references `Order` (order_id),
    constraint Payment_ibfk_2
        foreign key (payment_method_id) references PaymentMethod (payment_method_id)
);

create index payment_method_id
    on Payment (payment_method_id);

create index area_id
    on ServiceTable (area_id);

create table if not exists Store
(
    store_id     int auto_increment comment 'Mã cửa hàng'
        primary key,
    name         varchar(100)                       not null comment 'Tên cửa hàng',
    address      varchar(255)                       not null comment 'Địa chỉ',
    phone        varchar(15)                        not null comment 'Số điện thoại',
    email        varchar(100)                       null comment 'Email',
    opening_date date                               not null comment 'Ngày khai trương',
    tax_code     varchar(20)                        null comment 'Mã số thuế',
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table if not exists StoreHour
(
    store_hour_id int auto_increment comment 'Mã giờ mở cửa hàng'
        primary key,
    store_id      int                                null comment 'Mã cửa hàng',
    day_of_week   tinyint                            not null comment 'Thứ trong tuần (1-7, 1: Chủ nhật)',
    open_time     time                               not null comment 'Giờ mở cửa',
    close_time    time                               not null comment 'Giờ đóng cửa',
    created_at    datetime default CURRENT_TIMESTAMP null,
    updated_at    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint StoreHour_ibfk_1
        foreign key (store_id) references Store (store_id),
    constraint StoreHour_chk_1
        check (`day_of_week` between 1 and 7)
);

create index store_id
    on StoreHour (store_id);

create table if not exists Supplier
(
    supplier_id    int auto_increment comment 'Mã nhà cung cấp'
        primary key,
    name           varchar(100)                       not null comment 'Tên nhà cung cấp',
    contact_person varchar(100)                       null comment 'Người liên hệ',
    phone          varchar(15)                        not null comment 'Số điện thoại',
    email          varchar(100)                       null comment 'Email',
    address        text                               null comment 'Địa chỉ',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table if not exists InventoryTransaction
(
    transaction_id   int auto_increment comment 'Mã giao dịch kho'
        primary key,
    material_id      int                                null comment 'Mã nguyên vật liệu',
    supplier_id      int                                null comment 'Mã nhà cung cấp',
    manager_id       int                                null comment 'Mã quản lý',
    transaction_type enum ('INBOUND', 'OUTBOUND')       not null comment 'Loại giao dịch (INBOUND, OUTBOUND)',
    quantity         decimal(10, 2)                     not null comment 'Số lượng',
    transaction_date datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    unit_cost        decimal(10, 2)                     null comment 'Đơn giá',
    batch_number     varchar(50)                        null comment 'Số lô',
    notes            text                               null comment 'Ghi chú',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint InventoryTransaction_ibfk_1
        foreign key (material_id) references Material (material_id),
    constraint InventoryTransaction_ibfk_2
        foreign key (supplier_id) references Supplier (supplier_id),
    constraint InventoryTransaction_ibfk_3
        foreign key (manager_id) references Manager (manager_id)
);

create index manager_id
    on InventoryTransaction (manager_id);

create index material_id
    on InventoryTransaction (material_id);

create index supplier_id
    on InventoryTransaction (supplier_id);

create table if not exists Topping
(
    topping_id   int auto_increment comment 'Mã topping'
        primary key,
    name         varchar(50)                          not null comment 'Tên topping',
    description  text                                 null comment 'Mô tả',
    price        decimal(10, 2)                       not null comment 'Giá',
    is_available tinyint(1) default 1                 null comment 'Topping có sẵn (1: Có, 0: Không)',
    image        mediumblob                           null comment 'Hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint Topping_pk
        unique (name)
);

create table if not exists OrderTopping
(
    order_topping_id int auto_increment
        primary key,
    order_product_id int                                null,
    topping_id       int                                null comment 'Mã topping',
    quantity         int                                not null comment 'Số lượng',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderTopping_pk
        unique (order_product_id, topping_id),
    constraint OrderTopping_ibfk_1
        foreign key (order_product_id) references OrderProduct (order_product_id),
    constraint OrderTopping_ibfk_2
        foreign key (topping_id) references Topping (topping_id)
);

create index order_item_id
    on OrderTopping (order_product_id);

create index topping_id
    on OrderTopping (topping_id);


