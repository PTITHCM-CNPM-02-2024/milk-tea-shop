-- SQL Server 2022 Database Schema for Milk Tea Shop
-- Converted from MySQL to SQL Server

USE master;
GO

-- Create database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'MilkTeaShop')
BEGIN
    CREATE DATABASE MilkTeaShop 
    COLLATE SQL_Latin1_General_CP1_CI_AS;
END
GO

USE MilkTeaShop;
GO

-- Set required options for filtered indexes
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- Create area table
CREATE TABLE area (
    area_id SMALLINT IDENTITY(1,1) PRIMARY KEY, -- Mã khu vực
    name CHAR(3) NOT NULL, -- Tên khu vực
    description NVARCHAR(255) NULL, -- Mô tả
    max_tables INT NULL, -- Số bàn tối đa
    is_active BIT DEFAULT 1 NULL, -- Trạng thái hoạt động (1: Có, 0: Không)
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_area_name UNIQUE (name)
);

-- Create category table
CREATE TABLE category (
    category_id SMALLINT IDENTITY(1,1) PRIMARY KEY, -- Mã danh mục
    name NVARCHAR(100) NOT NULL, -- Tên danh mục
    description NVARCHAR(1000) NULL, -- Mô tả danh mục
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_category_name UNIQUE (name)
);

-- Create coupon table
CREATE TABLE coupon (
    coupon_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã coupon
    coupon NVARCHAR(15) NOT NULL, -- Mã giảm giá
    description NVARCHAR(1000) NULL, -- Mô tả
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Ngày tạo
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_coupon_coupon UNIQUE (coupon)
);

-- Create discount table
CREATE TABLE discount (
    discount_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã định danh duy nhất cho chương trình giảm giá
    name NVARCHAR(500) NOT NULL,
    description NVARCHAR(1000) NULL,
    coupon_id INT NOT NULL, -- Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá
    discount_value DECIMAL(11, 3) NOT NULL, -- Giá trị giảm giá (phần trăm hoặc số tiền cố định)
    discount_type NVARCHAR(20) NOT NULL CHECK (discount_type IN ('FIXED', 'PERCENTAGE')), -- Loại giảm giá
    min_required_order_value DECIMAL(11, 3) NOT NULL, -- Gái trị đơn hàng tối thiểu có thể áp dụng
    max_discount_amount DECIMAL(11, 3) NOT NULL, -- Giới hạn số tiền giảm giá tối đa
    min_required_product SMALLINT NULL, -- Số lượng sản phẩm tối thiểu cần mua để khuyến mãi
    valid_from DATETIME2 NULL, -- Thời điểm bắt đầu hiệu lực của chương trình giảm giá
    valid_until DATETIME2 NOT NULL, -- Thời điểm kết thúc hiệu lực của chương trình giảm giá
    current_uses INT NULL, -- Số lần đã sử dụng chương trình giảm giá này
    max_uses INT NULL, -- Số lần sử dụng tối đa cho chương trình giảm giá
    max_uses_per_customer SMALLINT NULL, -- Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này
    is_active BIT DEFAULT 1 NOT NULL, -- Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời điểm tạo chương trình giảm giá
    updated_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời điểm cập nhật gần nhất
    CONSTRAINT uk_discount_coupon_id UNIQUE (coupon_id),
    CONSTRAINT uk_discount_name UNIQUE (name),
    CONSTRAINT fk_discount_coupon FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);

-- Create membership_type table
CREATE TABLE membership_type (
    membership_type_id TINYINT IDENTITY(1,1) PRIMARY KEY, -- Mã loại thành viên
    type NVARCHAR(50) NOT NULL, -- Loại thành viên
    discount_value DECIMAL(10, 3) NOT NULL, -- Giá trị giảm giá
    discount_unit NVARCHAR(20) NOT NULL CHECK (discount_unit IN ('FIXED', 'PERCENTAGE')), -- Đơn vị giảm giá
    required_point INT NOT NULL, -- Điểm yêu cầu
    description NVARCHAR(255) NULL, -- Mô tả
    valid_until DATETIME2 NULL, -- Ngày hết hạn
    is_active BIT DEFAULT 1 NULL, -- Trạng thái (1: Hoạt động, 0: Không hoạt động)
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_membership_type_type UNIQUE (type),
    CONSTRAINT uk_membership_type_required_point UNIQUE (required_point)
);

-- Create payment_method table
CREATE TABLE payment_method (
    payment_method_id TINYINT IDENTITY(1,1) PRIMARY KEY, -- Mã phương thức thanh toán
    payment_name NVARCHAR(50) NOT NULL, -- Tên phương thức thanh toán
    payment_description NVARCHAR(255) NULL, -- Mô tả phương thức thanh toán
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_payment_method_payment_name UNIQUE (payment_name)
);

-- Create product table
CREATE TABLE product (
    product_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã sản phẩm
    category_id SMALLINT NULL, -- Mã danh mục
    name NVARCHAR(100) NOT NULL, -- Tên sản phẩm
    description NVARCHAR(1000) NULL, -- Mô tả sản phẩm
    is_available BIT DEFAULT 1 NULL, -- Sản phẩm có sẵn (1: Có, 0: Không)
    is_signature BIT DEFAULT 0 NULL, -- Sản phẩm đặc trưng (1: Có, 0: Không)
    image_path NVARCHAR(1000) NULL, -- Đường dẫn mô tả hình ảnh
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian tạo
    updated_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian cập nhật
    CONSTRAINT uk_product_name UNIQUE (name),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (category_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_product_category_id ON product (category_id);

-- Create role table
CREATE TABLE role (
    role_id TINYINT IDENTITY(1,1) PRIMARY KEY, -- Mã vai trò
    name NVARCHAR(50) NOT NULL, -- Tên vai trò (ví dụ: admin, staff, customer)
    description NVARCHAR(1000) NULL, -- Mô tả vai trò
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_role_name UNIQUE (name),
    CONSTRAINT chk_role_name CHECK (LEN(LTRIM(RTRIM(name))) > 0 AND LEN(name) <= 50)
);

-- Create account table
CREATE TABLE account (
    account_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã tài khoản
    role_id TINYINT NOT NULL, -- Mã vai trò
    username NVARCHAR(50) NOT NULL, -- Tên đăng nhập
    password_hash NVARCHAR(255) NOT NULL, -- Mật khẩu đã mã hóa
    is_active BIT DEFAULT 0 NULL, -- Tài khoản hoạt động (1: Có, 0: Không)
    is_locked BIT DEFAULT 0 NOT NULL, -- Tài khoản có bị khóa hay không (Có: 1, Không:0)
    last_login DATETIME2 NULL, -- Lần đăng nhập cuối
    token_version INT DEFAULT 0 NOT NULL, -- Kiểm tra tính hợp lệ của token
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian tạo
    updated_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian cập nhật
    CONSTRAINT uk_account_username UNIQUE (username),
    CONSTRAINT fk_account_role FOREIGN KEY (role_id) REFERENCES role (role_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_account_role_id ON account (role_id);

-- Create customer table
CREATE TABLE customer (
    customer_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã khách hàng
    membership_type_id TINYINT NOT NULL, -- Mã loại thành viên
    account_id INT NULL, -- Mã tài khoản
    last_name NVARCHAR(70) NULL, -- Họ
    first_name NVARCHAR(70) NULL, -- Tên
    phone NVARCHAR(15) NOT NULL, -- Số điện thoại
    email NVARCHAR(100) NULL, -- Email
    current_points INT DEFAULT 0 NULL, -- Điểm hiện tại
    gender NVARCHAR(10) NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')), -- Giới tính
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Ngày tạo
    updated_at DATETIME2 DEFAULT GETDATE() NULL, -- Ngày cập nhật
    CONSTRAINT uk_customer_phone UNIQUE (phone),
    CONSTRAINT fk_customer_membership_type FOREIGN KEY (membership_type_id) REFERENCES membership_type (membership_type_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_customer_account FOREIGN KEY (account_id) REFERENCES account (account_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_customer_account_id ON customer (account_id);
CREATE INDEX idx_customer_membership_type_id ON customer (membership_type_id);
-- Unique index cho email, chỉ áp dụng khi email không null (cho phép nhiều bản ghi có email = NULL)
CREATE UNIQUE INDEX idx_customer_email_unique ON customer (email) WHERE email IS NOT NULL;

-- Create employee table
CREATE TABLE employee (
    employee_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã nhân viên
    account_id INT NOT NULL, -- Mã tài khoản
    position NVARCHAR(50) NOT NULL, -- Chức vụ
    last_name NVARCHAR(70) NOT NULL, -- Họ
    first_name NVARCHAR(70) NOT NULL, -- Tên
    gender NVARCHAR(10) NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')), -- Giới tính
    phone NVARCHAR(15) NOT NULL, -- Số điện thoại
    email NVARCHAR(100) NOT NULL, -- Email
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_employee_account_id UNIQUE (account_id),
    CONSTRAINT uk_employee_email UNIQUE (email),
    CONSTRAINT uk_employee_phone UNIQUE (phone),
    CONSTRAINT fk_employee_account FOREIGN KEY (account_id) REFERENCES account (account_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Create manager table
CREATE TABLE manager (
    manager_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã quản lý
    account_id INT NOT NULL, -- Mã tài khoản
    last_name NVARCHAR(70) NOT NULL, -- Họ
    first_name NVARCHAR(70) NOT NULL, -- Tên
    gender NVARCHAR(10) NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')), -- Giới tính
    phone NVARCHAR(15) NOT NULL, -- Số điện thoại
    email NVARCHAR(100) NOT NULL, -- Email
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_manager_account_id UNIQUE (account_id),
    CONSTRAINT uk_manager_email UNIQUE (email),
    CONSTRAINT uk_manager_phone UNIQUE (phone),
    CONSTRAINT fk_manager_account FOREIGN KEY (account_id) REFERENCES account (account_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Create order table
CREATE TABLE [order] (
    order_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã đơn hàng
    customer_id INT NULL, -- Mã khách hàng
    employee_id INT NULL, -- Mã nhân viên
    order_time DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian đặt hàng
    total_amount DECIMAL(11, 3) NULL, -- Tổng tiền
    final_amount DECIMAL(11, 3) NULL, -- Thành tiền
    status NVARCHAR(20) NULL CHECK (status IN ('PROCESSING', 'CANCELLED', 'COMPLETED')), -- Trạng thái đơn hàng
    customize_note NVARCHAR(1000) NULL, -- Ghi chú tùy chỉnh
    point INT DEFAULT 1 NULL,
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_order_employee_id ON [order] (employee_id);

-- Create order_discount table
CREATE TABLE order_discount (
    order_discount_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã giảm giá đơn hàng
    order_id INT NOT NULL, -- Mã đơn hàng áp dụng giảm giá
    discount_id INT NOT NULL, -- Mã chương trình giảm giá được áp dụng
    discount_amount DECIMAL(11, 3) NOT NULL, -- Số tiền giảm giá được áp dụng
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_order_discount_order_discount UNIQUE (order_id, discount_id),
    CONSTRAINT fk_order_discount_order FOREIGN KEY (order_id) REFERENCES [order] (order_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_discount_discount FOREIGN KEY (discount_id) REFERENCES discount (discount_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Create payment table
CREATE TABLE payment (
    payment_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã thanh toán
    order_id INT NOT NULL, -- Mã đơn hàng
    payment_method_id TINYINT NOT NULL, -- Mã phương thức thanh toán
    status NVARCHAR(20) NULL CHECK (status IN ('PROCESSING', 'CANCELLED', 'PAID')), -- Trạng thái thanh toán
    amount_paid DECIMAL(11, 3) NULL, -- Số tiền đã trả
    change_amount DECIMAL(11, 3) DEFAULT 0.000 NULL, -- Tiền thừa
    payment_time DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian thanh toán
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES [order] (order_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_payment_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_method (payment_method_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_payment_payment_method_id ON payment (payment_method_id);

-- Create service_table table
CREATE TABLE service_table (
    table_id SMALLINT IDENTITY(1,1) PRIMARY KEY, -- Mã bàn
    area_id SMALLINT NULL, -- Mã khu vực
    table_number NVARCHAR(10) NOT NULL, -- Số bàn
    is_active BIT DEFAULT 1 NULL, -- Bàn có sẵn (1: Có, 0: Không)
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_service_table_area_table_number UNIQUE (area_id, table_number),
    CONSTRAINT fk_service_table_area FOREIGN KEY (area_id) REFERENCES area (area_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_service_table_area_id ON service_table (area_id);

-- Create order_table table
CREATE TABLE order_table (
    order_table_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã đơn hàng và bàn
    order_id INT NOT NULL, -- Mã đơn hàng
    table_id SMALLINT NOT NULL, -- Mã bàn
    check_in DATETIME2 NOT NULL, -- Thời gian vào bàn
    check_out DATETIME2 NULL, -- Thời gian rời bàn
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_order_table_order_table UNIQUE (order_id, table_id),
    CONSTRAINT fk_order_table_order FOREIGN KEY (order_id) REFERENCES [order] (order_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_table_service_table FOREIGN KEY (table_id) REFERENCES service_table (table_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_order_table_order_id ON order_table (order_id);
CREATE INDEX idx_order_table_table_id ON order_table (table_id);

-- Create store table
CREATE TABLE store (
    store_id TINYINT IDENTITY(1,1) PRIMARY KEY, -- Mã cửa hàng
    name NVARCHAR(100) NOT NULL, -- Tên cửa hàng
    address NVARCHAR(255) NOT NULL, -- Địa chỉ
    phone NVARCHAR(15) NOT NULL, -- Số điện thoại
    opening_time TIME NOT NULL, -- Thời gian mở cửa
    closing_time TIME NOT NULL,
    email NVARCHAR(100) NOT NULL, -- Email
    opening_date DATE NOT NULL, -- Ngày khai trương
    tax_code NVARCHAR(20) NOT NULL, -- Mã số thuế
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL
);

-- Create unit_of_measure table
CREATE TABLE unit_of_measure (
    unit_id SMALLINT IDENTITY(1,1) PRIMARY KEY, -- Mã đơn vị tính
    name NVARCHAR(30) NOT NULL, -- Tên đơn vị tính (cái, cc, ml, v.v.)
    symbol NVARCHAR(5) NOT NULL, -- Ký hiệu (cc, ml, v.v.)
    description NVARCHAR(1000) NULL, -- Mô tả
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_unit_of_measure_name UNIQUE (name),
    CONSTRAINT uk_unit_of_measure_symbol UNIQUE (symbol)
);

-- Create product_size table
CREATE TABLE product_size (
    size_id SMALLINT IDENTITY(1,1) PRIMARY KEY, -- Mã kích thước
    unit_id SMALLINT NOT NULL, -- Mã đơn vị tính
    name NVARCHAR(5) NOT NULL, -- Tên kích thước (ví dụ: S, M, L)
    quantity SMALLINT NOT NULL,
    description NVARCHAR(1000) NULL, -- Mô tả
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    CONSTRAINT uk_product_size_unit_name UNIQUE (unit_id, name),
    CONSTRAINT fk_product_size_unit_of_measure FOREIGN KEY (unit_id) REFERENCES unit_of_measure (unit_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Create product_price table
CREATE TABLE product_price (
    product_price_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã giá sản phẩm
    product_id INT NOT NULL, -- Mã sản phẩm
    size_id SMALLINT NOT NULL, -- Mã kích thước
    price DECIMAL(11, 3) NOT NULL, -- Giá
    created_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian tạo
    updated_at DATETIME2 DEFAULT GETDATE() NULL, -- Thời gian cập nhật
    CONSTRAINT uk_product_price_product_size UNIQUE (product_id, size_id),
    CONSTRAINT fk_product_price_product FOREIGN KEY (product_id) REFERENCES product (product_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_product_price_product_size FOREIGN KEY (size_id) REFERENCES product_size (size_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_product_price_product_id ON product_price (product_id);
CREATE INDEX idx_product_price_size_id ON product_price (size_id);

-- Create order_product table
CREATE TABLE order_product (
    order_product_id INT IDENTITY(1,1) PRIMARY KEY, -- Mã chi tiết đơn hàng
    order_id INT NOT NULL, -- Mã đơn hàng
    product_price_id INT NOT NULL, -- Mã giá sản phẩm
    quantity SMALLINT NOT NULL, -- Số lượng
    [option] NVARCHAR(500) NULL, -- Tùy chọn cho việc lựa chọn lượng đá, đường
    created_at DATETIME2 DEFAULT GETDATE() NULL,
    updated_at DATETIME2 DEFAULT GETDATE() NULL,
    -- Unique constraint sẽ được thay thế bằng filtered unique index để xử lý NULL values đúng cách
    CONSTRAINT fk_order_product_order FOREIGN KEY (order_id) REFERENCES [order] (order_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_product_product_price FOREIGN KEY (product_price_id) REFERENCES product_price (product_price_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE INDEX idx_order_product_order_id ON order_product (order_id);
CREATE INDEX idx_order_product_product_price_id ON order_product (product_price_id);

GO 