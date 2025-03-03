create table Area
(
    area_id     smallint unsigned auto_increment comment 'Mã khu vực'
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

create table Category
(
    category_id        smallint unsigned auto_increment comment 'Mã danh mục'
        primary key,
    name               varchar(100)                       not null comment 'Tên danh mục',
    description        varchar(1000)                      null comment 'Mô tả danh mục',
    parent_category_id smallint unsigned                  null comment 'Mã danh mục sản phẩm cha',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Category_pk
        unique (name),
    constraint Category_ibfk_1
        foreign key (parent_category_id) references Category (category_id)
);

create table Coupon
(
    coupon_id   int unsigned auto_increment comment 'Mã coupon'
        primary key,
    coupon      varchar(15)                          not null comment 'Mã giảm giá',
    description text                                 null comment 'Mô tả',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at  datetime   default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint coupon
        unique (coupon)
);

create table DiscountType
(
    discount_type_id smallint unsigned auto_increment comment 'Mã loại giảm giá'
        primary key,
    promotion_name   varchar(100)                       not null comment 'Tên loại giảm giá',
    description      text                               null comment 'Mô tả',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint DiscountType_pk
        unique (promotion_name)
);

create table Discount
(
    discount_id           int unsigned auto_increment comment 'Mã định danh duy nhất cho chương trình giảm giá'
        primary key,
    discount_type_id      smallint unsigned                      not null comment 'Liên kết với bảng DiscountType, xác định loại giảm giá (ví dụ: giảm giá theo mùa, khuyến mãi đặc biệt, v.v.)',
    coupon_id             int unsigned                           null comment 'Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá',
    apply_type            enum ('BEST', 'COMBINE')               not null comment 'Cách áp dụng khi có nhiều giảm giá: BEST - chọn giảm giá tốt nhất, COMBINE - kết hợp các giảm giá',
    discount_value        decimal(10, 2)                         not null comment 'Giá trị giảm giá (phần trăm hoặc số tiền cố định)',
    max_discount_amount   decimal(10, 2)                         null comment 'Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn',
    valid_from            datetime                               not null comment 'Thời điểm bắt đầu hiệu lực của chương trình giảm giá',
    valid_until           datetime                               not null comment 'Thời điểm kết thúc hiệu lực của chương trình giảm giá',
    current_uses          int unsigned default '0'               null comment 'Số lần đã sử dụng chương trình giảm giá này',
    max_uses              int unsigned                           null comment 'Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn',
    max_uses_per_customer smallint unsigned                      null comment 'Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn',
    is_active             tinyint(1)   default 1                 null comment 'Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động',
    created_at            datetime     default CURRENT_TIMESTAMP null comment 'Thời điểm tạo chương trình giảm giá',
    updated_at            datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời điểm cập nhật gần nhất',
    constraint Discount_ibfk_1
        foreign key (discount_type_id) references DiscountType (discount_type_id)
);

create table CategoryDiscount
(
    category_discount_id int auto_increment comment 'Mã định danh duy nhất cho mối quan hệ giữa danh mục và chương trình giảm giá'
        primary key,
    discount_id          int unsigned      not null comment 'Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng',
    category_id          smallint unsigned not null comment 'Liên kết với bảng Category, xác định danh mục được giảm giá',
    min_prod_quantity    tinyint unsigned  not null comment 'Số lượng sản phẩm tối thiểu từ danh mục này để áp dụng giảm giá',
    constraint CategoryDiscount_pk
        unique (discount_id, category_id),
    constraint CategoryDiscount_ibfk_1
        foreign key (discount_id) references Discount (discount_id),
    constraint CategoryDiscount_ibfk_2
        foreign key (category_id) references Category (category_id)
);

create index category_id
    on CategoryDiscount (category_id);

create index discount_id
    on CategoryDiscount (discount_id);

create table ExpenseCategory
(
    expense_category_id smallint unsigned auto_increment comment 'Mã loại chi phí'
        primary key,
    name                varchar(50)                        not null comment 'Tên loại chi phí (ví dụ: Nguyên liệu, Tiền điện, Lương nhân viên)',
    description         text                               null comment 'Mô tả',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ExpenseCategory_pk
        unique (name)
);

create table IncomeCategory
(
    income_category_id smallint unsigned auto_increment comment 'Mã loại thu nhập'
        primary key,
    name               varchar(50)                        not null comment 'Tên loại thu nhập (ví dụ: Bán hàng, Hoàn tiền)',
    description        text                               null comment 'Mô tả',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint IncomeCategory_pk
        unique (name)
);

create table Material
(
    material_id  mediumint unsigned auto_increment comment 'Mã nguyên vật liệu'
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

create table MembershipType
(
    membership_type_id tinyint unsigned auto_increment comment 'Mã loại thành viên'
        primary key,
    type               enum ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM') not null comment 'Loại thành viên',
    discount_value     decimal(10, 3)                                not null comment 'Giá trị giảm giá',
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

create table PaymentMethod
(
    payment_method_id   tinyint unsigned auto_increment comment 'Mã phương thức thanh toán'
        primary key,
    payment_name        varchar(50)                        not null comment 'Tên phương thức thanh toán',
    payment_description varchar(255)                       null comment 'Mô tả phương thức thanh toán',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint PaymentMethod_pk
        unique (payment_name)
);

create table Product
(
    product_id   mediumint unsigned auto_increment comment 'Mã sản phẩm'
        primary key,
    category_id  smallint unsigned                    null comment 'Mã danh mục',
    name         varchar(100)                         not null comment 'Tên sản phẩm',
    description  varchar(1000)                        null comment 'Mô tả sản phẩm',
    is_available tinyint(1) default 1                 null comment 'Sản phẩm có sẵn (1: Có, 0: Không)',
    is_signature tinyint(1) default 0                 null comment 'Sản phẩm đặc trưng (1: Có, 0: Không)',
    image_path   varchar(1000)                        null comment 'Đường dẫn mô tả hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    constraint Product_ibfk_1
        foreign key (category_id) references Category (category_id)
);

create index category_id
    on Product (category_id);

create table Role
(
    role_id     tinyint unsigned auto_increment comment 'Mã vai trò'
        primary key,
    name        varchar(50)                        not null comment 'Tên vai trò (ví dụ: admin, staff, customer)',
    description varchar(1000)                      null comment 'Mô tả vai trò',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Role_pk
        unique (name)
);

create table Account
(
    account_id    int unsigned auto_increment comment 'Mã tài khoản'
        primary key,
    role_id       tinyint unsigned                     null comment 'Mã vai trò',
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

create table Customer
(
    customer_id        int unsigned auto_increment comment 'Mã khách hàng'
        primary key,
    membership_type_id tinyint unsigned                    null comment 'Mã loại thành viên',
    account_id         int unsigned                        null comment 'Mã tài khoản',
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

create table Employee
(
    employee_id int unsigned auto_increment comment 'Mã nhân viên'
        primary key,
    account_id  int unsigned                       null comment 'Mã tài khoản',
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

create table Expense
(
    expense_id          int unsigned auto_increment comment 'Mã chi phí'
        primary key,
    account_id          int unsigned                       null comment 'Mã tài khoản',
    expense_category_id smallint unsigned                  null comment 'Mã loại chi phí',
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

create table Income
(
    income_id          int unsigned auto_increment comment 'Mã thu nhập'
        primary key,
    account_id         int unsigned                       null comment 'Mã tài khoản',
    income_category_id smallint unsigned                  null comment 'Mã loại thu nhập',
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

create table Manager
(
    manager_id int unsigned auto_increment comment 'Mã quản lý'
        primary key,
    account_id int unsigned                       null comment 'Mã tài khoản',
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

create table `Order`
(
    order_id        int unsigned auto_increment comment 'Mã đơn hàng'
        primary key,
    customer_id     int unsigned                             null comment 'Mã khách hàng',
    employee_id     int unsigned                             null comment 'Mã nhân viên',
    payment_id      int unsigned                             null comment 'Mã thanh toán',
    order_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian đặt hàng',
    total_amount    decimal(10, 2)                           not null comment 'Tổng tiền',
    discount_amount decimal(10, 2) default 0.00              null comment 'Số tiền giảm giá',
    final_amount    decimal(10, 2)                           not null comment 'Thành tiền',
    customize_note  text                                     null comment 'Ghi chú tùy chỉnh',
    created_at      datetime       default CURRENT_TIMESTAMP null,
    updated_at      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint Order_pk
        unique (payment_id),
    constraint Order_ibfk_1
        foreign key (customer_id) references Customer (customer_id),
    constraint Order_ibfk_2
        foreign key (employee_id) references Employee (employee_id)
);

create index employee_id
    on `Order` (employee_id);

create table Payment
(
    payment_id        int unsigned auto_increment comment 'Mã thanh toán'
        primary key,
    order_id          int unsigned                             null comment 'Mã đơn hàng',
    payment_method_id tinyint unsigned                         null comment 'Mã phương thức thanh toán',
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

alter table `Order`
    add constraint Order_ibfk_3
        foreign key (payment_id) references Payment (payment_id);

create index payment_method_id
    on Payment (payment_method_id);

create table RewardPointLog
(
    reward_point_log_id int unsigned auto_increment comment 'Mã lịch sử điểm thưởng'
        primary key,
    customer_id         int unsigned                       null comment 'Mã khách hàng',
    reward_point        int                                not null comment 'Số điểm thưởng',
    operation_type      enum ('EARN', 'REDEEM', 'ADJUST')  not null comment 'Loại thao tác (EARN, REDEEM, ADJUST)',
    created_at          datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint RewardPointLog_ibfk_1
        foreign key (customer_id) references Customer (customer_id)
);

create table ServiceTable
(
    table_id     smallint unsigned auto_increment comment 'Mã bàn'
        primary key,
    area_id      smallint unsigned                    null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_available tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ServiceTable_pk
        unique (area_id, table_number),
    constraint ServiceTable_ibfk_1
        foreign key (area_id) references Area (area_id)
);

create table OrderTable
(
    order_table_id int unsigned auto_increment comment 'Mã đơn hàng và bàn'
        primary key,
    order_id       int unsigned                       not null comment 'Mã đơn hàng',
    table_id       smallint unsigned                  not null comment 'Mã bàn',
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

create index area_id
    on ServiceTable (area_id);

create table Store
(
    store_id     tinyint unsigned auto_increment comment 'Mã cửa hàng'
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

create table StoreHour
(
    store_hour_id smallint unsigned auto_increment comment 'Mã giờ mở cửa hàng'
        primary key,
    store_id      tinyint unsigned                   null comment 'Mã cửa hàng',
    day_of_week   tinyint unsigned                   not null comment 'Thứ trong tuần (1-7, 1: Chủ nhật)',
    open_time     time                               not null comment 'Giờ mở cửa',
    close_time    time                               not null comment 'Giờ đóng cửa',
    created_at    datetime default CURRENT_TIMESTAMP null,
    updated_at    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint StoreHour_pk
        unique (store_id, day_of_week),
    constraint StoreHour_ibfk_1
        foreign key (store_id) references Store (store_id),
    constraint StoreHour_chk_1
        check (`day_of_week` between 1 and 7)
);

create index store_id
    on StoreHour (store_id);

create table Supplier
(
    supplier_id    int unsigned auto_increment comment 'Mã nhà cung cấp'
        primary key,
    name           varchar(100)                       not null comment 'Tên nhà cung cấp',
    contact_person varchar(100)                       null comment 'Người liên hệ',
    phone          varchar(15)                        not null comment 'Số điện thoại',
    email          varchar(100)                       null comment 'Email',
    address        text                               null comment 'Địa chỉ',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table InventoryTransaction
(
    transaction_id   int unsigned                       not null comment 'Mã giao dịch kho',
    material_id      mediumint unsigned                 null comment 'Mã nguyên vật liệu',
    supplier_id      int unsigned                       null comment 'Mã nhà cung cấp',
    manager_id       int unsigned                       null comment 'Mã quản lý',
    transaction_type enum ('INBOUND', 'OUTBOUND')       not null comment 'Loại giao dịch (INBOUND, OUTBOUND)',
    quantity         decimal(10, 2)                     not null comment 'Số lượng',
    transaction_date datetime default CURRENT_TIMESTAMP null comment 'Ngày giao dịch',
    unit_cost        decimal(10, 2)                     null comment 'Đơn giá',
    batch_number     varchar(50)                        null comment 'Số lô',
    notes            text                               null comment 'Ghi chú',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint `PRIMARY`
        primary key (transaction_id),
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

create table UnitOfMeasure
(
    unit_id     smallint unsigned auto_increment comment 'Mã đơn vị tính'
        primary key,
    name        varchar(30)                        not null comment 'Tên đơn vị tính (cái, cc, ml, v.v.)',
    symbol      varchar(10)                        null comment 'Ký hiệu (cc, ml, v.v.)',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint UnitOfMeasure_pk
        unique (name)
);

create table ProductSize
(
    size_id     smallint unsigned auto_increment comment 'Mã kích thước'
        primary key,
    unit_id     smallint unsigned                  null comment 'Mã đơn vị tính',
    name        varchar(5)                         not null comment 'Tên kích thước (ví dụ: S, M, L)',
    description text                               null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint ProductSize_pk
        unique (unit_id, name),
    constraint ProductSize_ibfk_1
        foreign key (unit_id) references UnitOfMeasure (unit_id)
);

create table ProductPrice
(
    product_price_id int unsigned auto_increment comment 'Mã giá sản phẩm'
        primary key,
    product_id       mediumint unsigned                 null comment 'Mã sản phẩm',
    size_id          smallint unsigned                  null comment 'Mã kích thước',
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

create table OrderProduct
(
    order_product_id int unsigned auto_increment comment 'Mã chi tiết đơn hàng'
        primary key,
    order_id         int unsigned                       not null comment 'Mã đơn hàng',
    product_price_id int unsigned                       not null comment 'Mã giá sản phẩm',
    quantity         smallint unsigned                  not null comment 'Số lượng',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderProduct_pk
        unique (order_id, product_price_id),
    constraint OrderProduct_ibfk_1
        foreign key (order_id) references `Order` (order_id),
    constraint OrderProduct_ibfk_2
        foreign key (product_price_id) references ProductPrice (product_price_id)
);

create table OrderDiscount
(
    order_discount_id int unsigned auto_increment comment 'Mã giảm giá đơn hàng'
        primary key,
    order_product_id  int unsigned                       not null comment 'Mã chi tiết đơn hàng (nếu giảm giá áp dụng cho sản phẩm cụ thể)',
    discount_id       int unsigned                       not null comment 'Mã chương trình giảm giá được áp dụng',
    discount_amount   decimal(10, 2)                     not null comment 'Số tiền giảm giá được áp dụng',
    created_at        datetime default CURRENT_TIMESTAMP null,
    updated_at        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint OrderDiscount_pk
        unique (order_product_id, discount_id),
    constraint OrderDiscount_ibfk_2
        foreign key (discount_id) references Discount (discount_id),
    constraint OrderDiscount_ibfk_3
        foreign key (order_product_id) references OrderProduct (order_product_id)
);

create index order_id
    on OrderProduct (order_id);

create index product_price_id
    on OrderProduct (product_price_id);

create table ProductDiscount
(
    product_discount_id int unsigned auto_increment comment 'Mã định danh duy nhất cho mối quan hệ giữa sản phẩm và chương trình giảm giá'
        primary key,
    discount_id         int unsigned     not null comment 'Liên kết với bảng Discount, xác định chương trình giảm giá áp dụng',
    product_price_id    int unsigned     not null comment 'Liên kết với bảng ProductPrice, xác định sản phẩm và kích thước được giảm giá',
    min_prod_quantity   tinyint unsigned not null comment 'Số lượng tối thiểu của sản phẩm để áp dụng giảm giá',
    constraint ProductDiscount_pk
        unique (discount_id, product_price_id),
    constraint ProductDiscount_ibfk_1
        foreign key (discount_id) references Discount (discount_id),
    constraint ProductDiscount_ibfk_2
        foreign key (product_price_id) references ProductPrice (product_price_id)
);

create index discount_id
    on ProductDiscount (discount_id);

create index product_price_id
    on ProductDiscount (product_price_id);

create index product_id
    on ProductPrice (product_id);

create index size_id
    on ProductPrice (size_id);


