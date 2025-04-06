create table Area
(
    area_id     smallint unsigned auto_increment comment 'Mã khu vực' primary key,
    name        char(3)                              not null comment 'Tên khu vực',
    description varchar(255)                         null comment 'Mô tả',
    max_tables  int                                  null comment 'Số bàn tối đa',
    is_active   tinyint(1) default 1                 null comment 'Trạng thái hoạt động (1: Có, 0: Không)',
    created_at  datetime   default CURRENT_TIMESTAMP null,
    updated_at  datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key Area_pk (name)
);

create table Category
(
    category_id        smallint unsigned auto_increment comment 'Mã danh mục' primary key,
    name               varchar(100)                       not null comment 'Tên danh mục',
    description        varchar(1000)                      null comment 'Mô tả danh mục',
    parent_category_id smallint unsigned                  null comment 'Mã danh mục sản phẩm cha',
    created_at         datetime default CURRENT_TIMESTAMP null,
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key Category_pk (name),
    foreign key Category_ibfk_1 (parent_category_id) references Category (category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table Coupon
(
    coupon_id   int unsigned auto_increment comment 'Mã coupon' primary key,
    coupon      varchar(15)                        not null comment 'Mã giảm giá',
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key coupon (coupon)
);

create table Discount
(
    discount_id              int unsigned auto_increment comment 'Mã định danh duy nhất cho chương trình giảm giá' primary key,
    name                     varchar(500)                         not null,
    description              varchar(1000)                        null,
    coupon_id                int unsigned                         not null comment 'Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá',
    discount_value           decimal(11, 3)                       not null comment 'Giá trị giảm giá (phần trăm hoặc số tiền cố định)',
    discount_type            enum ('FIXED', 'PERCENTAGE')         not null comment 'Loại giảm giá enum ("PERCENTAGE", "FIXED")',
    min_required_order_value decimal(11, 3)                       not null comment 'Gái trị đơn hàng tối thiểu có thể áp dụng',
    max_discount_amount      decimal(11, 3)                       not null comment 'Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn',
    min_required_product     smallint unsigned                    null comment 'Số lượng sản phẩm tối thiểu cần mua để khuyến mãi',
    valid_from               datetime                             null comment 'Thời điểm bắt đầu hiệu lực của chương trình giảm giá',
    valid_until              datetime                             not null comment 'Thời điểm kết thúc hiệu lực của chương trình giảm giá',
    current_uses             int unsigned                         null comment 'Số lần đã sử dụng chương trình giảm giá này',
    max_uses                 int unsigned                         null comment 'Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn',
    max_uses_per_customer    smallint unsigned                    null comment 'Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn',
    is_active                tinyint(1) default 1                 not null comment 'Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động',
    created_at               datetime   default CURRENT_TIMESTAMP null comment 'Thời điểm tạo chương trình giảm giá',
    updated_at               datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời điểm cập nhật gần nhất',
    unique key Discount_pk (coupon_id),
    unique key Discount_pk_2 (name),
    foreign key Discount_ibfk_2 (coupon_id) references Coupon (coupon_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table MembershipType
(
    membership_type_id tinyint unsigned auto_increment comment 'Mã loại thành viên' primary key,
    type               varchar(50)                          not null comment 'Loại thành viên',
    discount_value     decimal(10, 3)                       not null comment 'Giá trị giảm giá',
    discount_unit      enum ('FIXED', 'PERCENTAGE')         not null comment 'Đơn vị giảm giá (PERCENT, FIXED)',
    required_point     int                                  not null comment 'Điểm yêu cầu',
    description        varchar(255)                         null comment 'Mô tả',
    valid_until        datetime                             null comment 'Ngày hết hạn',
    is_active          tinyint(1) default 1                 null comment 'Trạng thái (1: Hoạt động, 0: Không hoạt động)',
    created_at         datetime   default CURRENT_TIMESTAMP null,
    updated_at         datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key MembershipType_pk (type),
    unique key MembershipType_pk_2 (required_point)
);

create table PaymentMethod
(
    payment_method_id   tinyint unsigned auto_increment comment 'Mã phương thức thanh toán' primary key,
    payment_name        varchar(50)                        not null comment 'Tên phương thức thanh toán',
    payment_description varchar(255)                       null comment 'Mô tả phương thức thanh toán',
    created_at          datetime default CURRENT_TIMESTAMP null,
    updated_at          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key PaymentMethod_pk (payment_name)
);

create table Product
(
    product_id   mediumint unsigned auto_increment comment 'Mã sản phẩm' primary key,
    category_id  smallint unsigned                    null comment 'Mã danh mục',
    name         varchar(100)                         not null comment 'Tên sản phẩm',
    description  varchar(1000)                        null comment 'Mô tả sản phẩm',
    is_available tinyint(1) default 1                 null comment 'Sản phẩm có sẵn (1: Có, 0: Không)',
    is_signature tinyint(1) default 0                 null comment 'Sản phẩm đặc trưng (1: Có, 0: Không)',
    image_path   varchar(1000)                        null comment 'Đường dẫn mô tả hình ảnh',
    created_at   datetime   default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    index category_id (category_id),
    foreign key Product_ibfk_1 (category_id) references Category (category_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table Role
(
    role_id     tinyint unsigned auto_increment comment 'Mã vai trò' primary key,
    name        varchar(50)                        not null comment 'Tên vai trò (ví dụ: admin, staff, customer)',
    description varchar(1000)                      null comment 'Mô tả vai trò',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key Role_pk (name)
);

create table Account
(
    account_id    int unsigned auto_increment comment 'Mã tài khoản' primary key,
    role_id       tinyint unsigned                       not null comment 'Mã vai trò',
    username      varchar(50)                            not null comment 'Tên đăng nhập',
    password_hash varchar(255)                           not null comment 'Mật khẩu đã mã hóa',
    is_active     tinyint(1)   default 0                 null comment 'Tài khoản hoạt động (1: Có, 0: Không)',
    is_locked     tinyint(1)   default 0                 not null comment 'Tài khoản có bị khóa hay không (Có: 1, Không:0)',
    last_login    timestamp                              null comment 'Lần đăng nhập cuối',
    token_version int unsigned default '0'               not null comment 'Kiểm tra tính hợp lệ của token',
    created_at    datetime     default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at    datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    unique key username (username),
    index role_id (role_id),
    foreign key Account_ibfk_1 (role_id) references Role (role_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table Customer
(
    customer_id        int unsigned auto_increment comment 'Mã khách hàng' primary key,
    membership_type_id tinyint unsigned                    not null comment 'Mã loại thành viên',
    account_id         int unsigned                        null comment 'Mã tài khoản',
    last_name          varchar(70)                         null comment 'Họ',
    first_name         varchar(70)                         null comment 'Tên',
    phone              varchar(15)                         not null comment 'Số điện thoại',
    email              varchar(100)                        null comment 'Email',
    current_points     int       default 0                 null comment 'Điểm hiện tại',
    gender             enum ('MALE', 'FEMALE', 'OTHER')    null comment 'Giới tính',
    created_at         timestamp default CURRENT_TIMESTAMP null comment 'Ngày tạo',
    updated_at         timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Ngày cập nhật',
    unique key email (email),
    unique key phone (phone),
    index account_id (account_id),
    index membership_type_id (membership_type_id),
    foreign key Customer_ibfk_1 (membership_type_id) references MembershipType (membership_type_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    foreign key Customer_ibfk_2 (account_id) references Account (account_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table Employee
(
    employee_id int unsigned auto_increment comment 'Mã nhân viên' primary key,
    account_id  int unsigned                       null comment 'Mã tài khoản',
    position    varchar(50)                        not null comment 'Chức vụ',
    last_name   varchar(70)                        not null comment 'Họ',
    first_name  varchar(70)                        not null comment 'Tên',
    gender      enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone       varchar(15)                        not null comment 'Số điện thoại',
    email       varchar(100)                       not null comment 'Email',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key account_id (account_id),
    foreign key Employee_ibfk_1 (account_id) references Account (account_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table Manager
(
    manager_id int unsigned auto_increment comment 'Mã quản lý' primary key,
    account_id int unsigned                       null comment 'Mã tài khoản',
    last_name  varchar(70)                        not null comment 'Họ',
    first_name varchar(70)                        not null comment 'Tên',
    gender     enum ('MALE', 'FEMALE', 'OTHER')   null comment 'Giới tính',
    phone      varchar(15)                        not null comment 'Số điện thoại',
    email      varchar(100)                       not null comment 'Email',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key account_id (account_id),
    foreign key Manager_ibfk_1 (account_id) references Account (account_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table `Order`
(
    order_id       int unsigned auto_increment comment 'Mã đơn hàng' primary key,
    customer_id    int unsigned                                  null comment 'Mã khách hàng',
    employee_id    int unsigned                                  null comment 'Mã nhân viên',
    order_time     timestamp    default CURRENT_TIMESTAMP        null comment 'Thời gian đặt hàng',
    total_amount   decimal(11, 3)                                null comment 'Tổng tiền',
    final_amount   decimal(11, 3)                                null comment 'Thành tiền',
    status         enum ('PROCESSING', 'CANCELLED', 'COMPLETED') null comment 'Trạng thái đơn hàng',
    customize_note varchar(1000)                                 null comment 'Ghi chú tùy chỉnh',
    point          int unsigned default '1'                      null,
    created_at     datetime     default CURRENT_TIMESTAMP        null,
    updated_at     datetime     default CURRENT_TIMESTAMP        null on update CURRENT_TIMESTAMP,
    index employee_id (employee_id),
    foreign key Order_ibfk_1 (customer_id) references Customer (customer_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    foreign key Order_ibfk_2 (employee_id) references Employee (employee_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table OrderDiscount
(
    order_discount_id int unsigned auto_increment comment 'Mã giảm giá đơn hàng' primary key,
    order_id          int unsigned                       not null comment 'Mã đơn  hàng áp dụng giảm giá',
    discount_id       int unsigned                       not null comment 'Mã chương trình giảm giá được áp dụng',
    discount_amount   decimal(11, 3)                     not null comment 'Số tiền giảm giá được áp dụng',
    created_at        datetime default CURRENT_TIMESTAMP null,
    updated_at        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key OrderDiscount_pk (order_id, discount_id),
    foreign key OrderDiscount_ibfk_1 (order_id) references `Order` (order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign key OrderDiscount_ibfk_2 (discount_id) references Discount (discount_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table Payment
(
    payment_id        int unsigned auto_increment comment 'Mã thanh toán' primary key,
    order_id          int unsigned                             not null comment 'Mã đơn hàng',
    payment_method_id tinyint unsigned                         not null comment 'Mã phương thức thanh toán',
    status            enum ('PROCESSING', 'CANCELLED', 'PAID') null comment 'Trạng thái thanh toán',
    amount_paid       decimal(11, 3)                           null comment 'Số tiền đã trả',
    change_amount     decimal(11, 3) default 0.000             null comment 'Tiền thừa',
    payment_time      timestamp      default CURRENT_TIMESTAMP null comment 'Thời gian thanh toán',
    created_at        datetime       default CURRENT_TIMESTAMP null,
    updated_at        datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    index payment_method_id (payment_method_id),
    foreign key Payment_ibfk_1 (order_id) references `Order` (order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign key Payment_ibfk_2 (payment_method_id) references PaymentMethod (payment_method_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table ServiceTable
(
    table_id     smallint unsigned auto_increment comment 'Mã bàn' primary key,
    area_id      smallint unsigned                    null comment 'Mã khu vực',
    table_number varchar(10)                          not null comment 'Số bàn',
    is_active    tinyint(1) default 1                 null comment 'Bàn có sẵn (1: Có, 0: Không)',
    created_at   datetime   default CURRENT_TIMESTAMP null,
    updated_at   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key ServiceTable_pk (area_id, table_number),
    index area_id (area_id),
    foreign key ServiceTable_ibfk_1 (area_id) references Area (area_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

create table OrderTable
(
    order_table_id int unsigned auto_increment comment 'Mã đơn hàng và bàn' primary key,
    order_id       int unsigned                       not null comment 'Mã đơn hàng',
    table_id       smallint unsigned                  not null comment 'Mã bàn',
    check_in       datetime                           not null comment 'Thời gian vào bàn',
    check_out      datetime                           null comment 'Thời gian rời bàn',
    created_at     datetime default CURRENT_TIMESTAMP null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key OrderTable_pk (order_id, table_id),
    index order_id (order_id),
    index table_id (table_id),
    foreign key OrderTable_ibfk_1 (order_id) references `Order` (order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign key OrderTable_ibfk_2 (table_id) references ServiceTable (table_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table Store
(
    store_id     tinyint unsigned auto_increment comment 'Mã cửa hàng' primary key,
    name         varchar(100)                       not null comment 'Tên cửa hàng',
    address      varchar(255)                       not null comment 'Địa chỉ',
    phone        varchar(15)                        not null comment 'Số điện thoại',
    opening_time time                               not null comment 'Thời gian mở cửa',
    closing_time time                               not null,
    email        varchar(100)                       not null comment 'Email',
    opening_date date                               not null comment 'Ngày khai trương',
    tax_code     varchar(20)                        not null comment 'Mã số thuế',
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table UnitOfMeasure
(
    unit_id     smallint unsigned auto_increment comment 'Mã đơn vị tính' primary key,
    name        varchar(30)                        not null comment 'Tên đơn vị tính (cái, cc, ml, v.v.)',
    symbol      varchar(5)                         not null comment 'Ký hiệu (cc, ml, v.v.)',
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key UnitOfMeasure_pk (name),
    unique key UnitOfMeasure_pk_2 (symbol)
);

create table ProductSize
(
    size_id     smallint unsigned auto_increment comment 'Mã kích thước' primary key,
    unit_id     smallint unsigned                  not null comment 'Mã đơn vị tính',
    name        varchar(5)                         not null comment 'Tên kích thước (ví dụ: S, M, L)',
    quantity    smallint unsigned                  not null,
    description varchar(1000)                      null comment 'Mô tả',
    created_at  datetime default CURRENT_TIMESTAMP null,
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key ProductSize_pk (unit_id, name),
    foreign key ProductSize_ibfk_1 (unit_id) references UnitOfMeasure (unit_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

create table ProductPrice
(
    product_price_id int unsigned auto_increment comment 'Mã giá sản phẩm' primary key,
    product_id       mediumint unsigned                 null comment 'Mã sản phẩm',
    size_id          smallint unsigned                  null comment 'Mã kích thước',
    price            decimal(11, 3)                     not null comment 'Giá',
    created_at       datetime default CURRENT_TIMESTAMP null comment 'Thời gian tạo',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment 'Thời gian cập nhật',
    unique key ProductPrice_pk (product_id, size_id),
    index product_id (product_id),
    index size_id (size_id),
    foreign key ProductPrice_ibfk_1 (product_id) references Product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign key ProductPrice_ibfk_2 (size_id) references ProductSize (size_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

create table OrderProduct
(
    order_product_id int unsigned auto_increment comment 'Mã chi tiết đơn hàng' primary key,
    order_id         int unsigned                       not null comment 'Mã đơn hàng',
    product_price_id int unsigned                       not null comment 'Mã giá sản phẩm',
    quantity         smallint unsigned                  not null comment 'Số lượng',
    `option`         varchar(500)                       null comment 'Tùy chọn cho việc lựa chọn lượng đá, đường ',
    created_at       datetime default CURRENT_TIMESTAMP null,
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    unique key OrderProduct_pk (order_id, product_price_id),
    index order_id (order_id),
    index product_price_id (product_price_id),
    foreign key OrderProduct_ibfk_1 (order_id) references `Order` (order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign key OrderProduct_ibfk_2 (product_price_id) references ProductPrice (product_price_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);




