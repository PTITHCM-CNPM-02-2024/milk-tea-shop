```mermaid
classDiagram
direction LR
class Account {
   tinyint unsigned role_id  /* Mã vai trò */
   varchar(50) username  /* Tên đăng nhập */
   varchar(255) password_hash  /* Mật khẩu đã mã hóa */
   is_active  /* Tài khoản hoạt động (1: Có, 0: Không) */ tinyint(1)
   is_locked  /* Tài khoản có bị khóa hay không (Có: 1, Không:0) */ tinyint(1)
   timestamp last_login  /* Lần đăng nhập cuối */
   int unsigned token_version  /* Kiểm tra tính hợp lệ của token */
   datetime created_at  /* Thời gian tạo */
   datetime updated_at  /* Thời gian cập nhật */
   int unsigned account_id  /* Mã tài khoản */
}
class Area {
   char(3) name  /* Tên khu vực */
   varchar(255) description  /* Mô tả */
   int max_tables  /* Số bàn tối đa */
   is_active  /* Trạng thái hoạt động (1: Có, 0: Không) */ tinyint(1)
   datetime created_at
   datetime updated_at
   smallint unsigned area_id  /* Mã khu vực */
}
class Category {
   varchar(100) name  /* Tên danh mục */
   varchar(1000) description  /* Mô tả danh mục */
   smallint unsigned parent_category_id  /* Mã danh mục sản phẩm cha */
   datetime created_at
   datetime updated_at
   smallint unsigned category_id  /* Mã danh mục */
}
class Coupon {
   varchar(15) coupon  /* Mã giảm giá */
   varchar(1000) description  /* Mô tả */
   datetime created_at  /* Ngày tạo */
   datetime updated_at
   int unsigned coupon_id  /* Mã coupon */
}
class Customer {
   tinyint unsigned membership_type_id  /* Mã loại thành viên */
   int unsigned account_id  /* Mã tài khoản */
   varchar(70) last_name  /* Họ */
   varchar(70) first_name  /* Tên */
   varchar(15) phone  /* Số điện thoại */
   varchar(100) email  /* Email */
   int current_points  /* Điểm hiện tại */
   enum('male', 'female', 'other') gender  /* Giới tính */
   timestamp created_at  /* Ngày tạo */
   timestamp updated_at  /* Ngày cập nhật */
   int unsigned customer_id  /* Mã khách hàng */
}
class Discount {
   varchar(500) name
   varchar(1000) description
   coupon_id  /* Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã ... */ int unsigned
   discount_value  /* Giá trị giảm giá (phần trăm hoặc số tiền cố định) */ decimal(11,3)
   discount_type  /* Loại giảm giá enum ("PERCENTAGE", "FIXED") */ enum('fixed', 'percentage')
   decimal(11,3) min_required_order_value  /* Gái trị đơn hàng tối thiểu có thể áp dụng */
   decimal(11,3) max_discount_amount  /* Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn */
   smallint unsigned min_required_product  /* Số lượng sản phẩm tối thiểu cần mua để khuyến mãi */
   datetime valid_from  /* Thời điểm bắt đầu hiệu lực của chương trình giảm giá */
   datetime valid_until  /* Thời điểm kết thúc hiệu lực của chương trình giảm giá */
   int unsigned current_uses  /* Số lần đã sử dụng chương trình giảm giá này */
   int unsigned max_uses  /* Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu khô... */
   smallint unsigned max_uses_per_customer  /* Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm g... */
   tinyint(1) is_active  /* Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động */
   datetime created_at  /* Thời điểm tạo chương trình giảm giá */
   datetime updated_at  /* Thời điểm cập nhật gần nhất */
   int unsigned discount_id  /* Mã định danh duy nhất cho chương trình giảm giá */
}
class Employee {
   int unsigned account_id  /* Mã tài khoản */
   varchar(50) position  /* Chức vụ */
   varchar(70) last_name  /* Họ */
   varchar(70) first_name  /* Tên */
   enum('male', 'female', 'other') gender  /* Giới tính */
   varchar(15) phone  /* Số điện thoại */
   varchar(100) email  /* Email */
   datetime created_at
   datetime updated_at
   int unsigned employee_id  /* Mã nhân viên */
}
class Manager {
   int unsigned account_id  /* Mã tài khoản */
   varchar(70) last_name  /* Họ */
   varchar(70) first_name  /* Tên */
   enum('male', 'female', 'other') gender  /* Giới tính */
   varchar(15) phone  /* Số điện thoại */
   varchar(100) email  /* Email */
   datetime created_at
   datetime updated_at
   int unsigned manager_id  /* Mã quản lý */
}
class MembershipType {
   varchar(50) type  /* Loại thành viên */
   decimal(10,3) discount_value  /* Giá trị giảm giá */
   discount_unit  /* Đơn vị giảm giá (PERCENT, FIXED) */ enum('fixed', 'percentage')
   int required_point  /* Điểm yêu cầu */
   varchar(255) description  /* Mô tả */
   datetime valid_until  /* Ngày hết hạn */
   is_active  /* Trạng thái (1: Hoạt động, 0: Không hoạt động) */ tinyint(1)
   datetime created_at
   datetime updated_at
   tinyint unsigned membership_type_id  /* Mã loại thành viên */
}
class Order {
   int unsigned customer_id  /* Mã khách hàng */
   int unsigned employee_id  /* Mã nhân viên */
   timestamp order_time  /* Thời gian đặt hàng */
   decimal(11,3) total_amount  /* Tổng tiền */
   decimal(11,3) final_amount  /* Thành tiền */
   varchar(1000) customize_note  /* Ghi chú tùy chỉnh */
   datetime created_at
   datetime updated_at
   int unsigned order_id  /* Mã đơn hàng */
}
class OrderDiscount {
   order_product_id  /* Mã chi tiết đơn hàng (nếu giảm giá áp dụng cho sản phẩm cụ thể) */ int unsigned
   int unsigned discount_id  /* Mã chương trình giảm giá được áp dụng */
   decimal(11,3) discount_amount  /* Số tiền giảm giá được áp dụng */
   datetime created_at
   datetime updated_at
   int unsigned order_discount_id  /* Mã giảm giá đơn hàng */
}
class OrderProduct {
   int unsigned order_id  /* Mã đơn hàng */
   int unsigned product_price_id  /* Mã giá sản phẩm */
   int unsigned parent_order_product_id  /* Mã đặt hàng sản phẩm gốc, khi sản phẩm ở hàng này được đặt là... */
   smallint unsigned quantity  /* Số lượng */
   varchar(500) option  /* Tùy chọn cho việc lựa chọn lượng đá, đường  */
   datetime created_at
   datetime updated_at
   int unsigned order_product_id  /* Mã chi tiết đơn hàng */
}
class OrderTable {
   int unsigned order_id  /* Mã đơn hàng */
   smallint unsigned table_id  /* Mã bàn */
   datetime check_in  /* Thời gian vào bàn */
   datetime check_out  /* Thời gian rời bàn */
   datetime created_at
   datetime updated_at
   int unsigned order_table_id  /* Mã đơn hàng và bàn */
}
class Payment {
   int unsigned order_id  /* Mã đơn hàng */
   tinyint unsigned payment_method_id  /* Mã phương thức thanh toán */
   decimal(11,3) amount_paid  /* Số tiền đã trả */
   decimal(11,3) change_amount  /* Tiền thừa */
   timestamp payment_time  /* Thời gian thanh toán */
   datetime created_at
   datetime updated_at
   int unsigned payment_id  /* Mã thanh toán */
}
class PaymentMethod {
   varchar(50) payment_name  /* Tên phương thức thanh toán */
   varchar(255) payment_description  /* Mô tả phương thức thanh toán */
   datetime created_at
   datetime updated_at
   tinyint unsigned payment_method_id  /* Mã phương thức thanh toán */
}
class Product {
   smallint unsigned category_id  /* Mã danh mục */
   varchar(100) name  /* Tên sản phẩm */
   varchar(1000) description  /* Mô tả sản phẩm */
   is_available  /* Sản phẩm có sẵn (1: Có, 0: Không) */ tinyint(1)
   is_signature  /* Sản phẩm đặc trưng (1: Có, 0: Không) */ tinyint(1)
   varchar(1000) image_path  /* Đường dẫn mô tả hình ảnh */
   datetime created_at  /* Thời gian tạo */
   datetime updated_at  /* Thời gian cập nhật */
   mediumint unsigned product_id  /* Mã sản phẩm */
}
class ProductPrice {
   mediumint unsigned product_id  /* Mã sản phẩm */
   smallint unsigned size_id  /* Mã kích thước */
   decimal(11,3) price  /* Giá */
   datetime created_at  /* Thời gian tạo */
   datetime updated_at  /* Thời gian cập nhật */
   int unsigned product_price_id  /* Mã giá sản phẩm */
}
class ProductSize {
   smallint unsigned unit_id  /* Mã đơn vị tính */
   name  /* Tên kích thước (ví dụ: S, M, L) */ varchar(5)
   smallint unsigned quantity
   varchar(1000) description  /* Mô tả */
   datetime created_at
   datetime updated_at
   smallint unsigned size_id  /* Mã kích thước */
}
class RewardPointLog {
   int unsigned customer_id  /* Mã khách hàng */
   int unsigned order_id
   int reward_point  /* Số điểm thưởng */
   datetime created_at  /* Thời gian tạo */
   datetime updated_at
   int unsigned reward_point_log_id  /* Mã lịch sử điểm thưởng */
}
class Role {
   name  /* Tên vai trò (ví dụ: admin, staff, customer) */ varchar(50)
   varchar(1000) description  /* Mô tả vai trò */
   datetime created_at
   datetime updated_at
   tinyint unsigned role_id  /* Mã vai trò */
}
class ServiceTable {
   smallint unsigned area_id  /* Mã khu vực */
   varchar(10) table_number  /* Số bàn */
   is_active  /* Bàn có sẵn (1: Có, 0: Không) */ tinyint(1)
   datetime created_at
   datetime updated_at
   smallint unsigned table_id  /* Mã bàn */
}
class Store {
   varchar(100) name  /* Tên cửa hàng */
   varchar(255) address  /* Địa chỉ */
   varchar(15) phone  /* Số điện thoại */
   time opening_time  /* Thời gian mở cửa */
   time closing_time
   varchar(100) email  /* Email */
   date opening_date  /* Ngày khai trương */
   varchar(20) tax_code  /* Mã số thuế */
   datetime created_at
   datetime updated_at
   tinyint unsigned store_id  /* Mã cửa hàng */
}
class UnitOfMeasure {
   name  /* Tên đơn vị tính (cái, cc, ml, v.v.) */ varchar(30)
   symbol  /* Ký hiệu (cc, ml, v.v.) */ varchar(5)
   varchar(1000) description  /* Mô tả */
   datetime created_at
   datetime updated_at
   smallint unsigned unit_id  /* Mã đơn vị tính */
}
class flyway_schema_history {
   varchar(50) version
   varchar(200) description
   varchar(20) type
   varchar(1000) script
   int checksum
   varchar(100) installed_by
   timestamp installed_on
   int execution_time
   tinyint(1) success
   int installed_rank
}

Account  -->  Role : role_id
Category  -->  Category : parent_category_id:category_id
Customer  -->  Account : account_id
Customer  -->  MembershipType : membership_type_id
Discount  -->  Coupon : coupon_id
Employee  -->  Account : account_id
Manager  -->  Account : account_id
Order  -->  Customer : customer_id
Order  -->  Employee : employee_id
OrderDiscount  -->  Discount : discount_id
OrderDiscount  -->  OrderProduct : order_product_id
OrderProduct  -->  Order : order_id
OrderProduct  -->  OrderProduct : parent_order_product_id:order_product_id
OrderProduct  -->  ProductPrice : product_price_id
OrderTable  -->  Order : order_id
OrderTable  -->  ServiceTable : table_id
Payment  -->  Order : order_id
Payment  -->  PaymentMethod : payment_method_id
Product  -->  Category : category_id
ProductPrice  -->  Product : product_id
ProductPrice  -->  ProductSize : size_id
ProductSize  -->  UnitOfMeasure : unit_id
RewardPointLog  -->  Customer : customer_id
RewardPointLog  -->  Order : order_id
ServiceTable  -->  Area : area_id
```