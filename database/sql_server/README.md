# Milk Tea Shop - SQL Server 2022 Database

Dự án này đã được chuyển đổi từ MySQL sang SQL Server 2022 bằng Docker Compose.

## Yêu cầu hệ thống

- Docker
- Docker Compose
- Ít nhất 4GB RAM trống cho SQL Server container

## Cách khởi chạy

### 1. Khởi động SQL Server 2022

```bash
docker-compose up -d
```

### 2. Kiểm tra trạng thái container

```bash
docker-compose ps
```

### 3. Xem logs

```bash
docker-compose logs sqlserver
```

## Thông tin kết nối

- **Server**: localhost,1433
- **Username**: sa
- **Password**: MilkTeaShop@2024
- **Database**: MilkTeaShop

## Chuỗi kết nối

```
Server=localhost,1433;Database=MilkTeaShop;User Id=sa;Password=MilkTeaShop@2024;TrustServerCertificate=true;
```

## Cấu trúc database

Database đã được chuyển đổi hoàn toàn từ MySQL sang SQL Server với những thay đổi chính:

### Thay đổi kiểu dữ liệu
- `AUTO_INCREMENT` → `IDENTITY(1,1)`
- `TINYINT(1)` → `BIT`
- `VARCHAR()` → `NVARCHAR()` (hỗ trợ Unicode)
- `DATETIME` → `DATETIME2`
- `CURRENT_TIMESTAMP` → `GETDATE()`
- `ENUM` → `NVARCHAR` với `CHECK` constraint

### Thay đổi cú pháp
- `\`order\`` → `[order]` (từ khóa được bảo vệ)
- MySQL triggers → SQL Server triggers (INSTEAD OF)
- `DELIMITER //` → `GO`
- `SIGNAL SQLSTATE` → `THROW`

### Triggers đã chuyển đổi
Tất cả triggers bảo vệ dữ liệu mặc định đã được chuyển đổi sang dạng SQL Server INSTEAD OF triggers.

### Tính năng nâng cao
- **Membership Management**: Tự động quản lý thành viên, tính điểm, cấp loại thành viên
- **Scheduled Events**: Bảo trì hệ thống tự động (vô hiệu hóa discount hết hạn, reset membership)
- **Business Logic Triggers**: Validation và tự động tính toán
- **Reporting System**: Views và stored procedures cho báo cáo
- **SQL Server Agent Integration**: Hướng dẫn thiết lập jobs tự động

## Cấu trúc file

```
├── docker-compose.yml                  # Docker Compose configuration
├── init/
│   ├── 001_base_line.sql              # Schema database chính
│   ├── 002_init_default_data.sql      # Dữ liệu mặc định
│   ├── 003_init_procedures.sql        # Stored procedures
│   ├── 004_init_triggers.sql          # Business logic triggers
│   ├── 006_membership_trigger_event.sql # Membership management events
│   ├── 007_scheduled_events.sql       # Scheduled maintenance events
│   ├── 008_reporting_procedures_views.sql # Reporting system
│   ├── 009_user_grant.sql             # User management procedures
│   ├── 010_create_agent_jobs.sql      # SQL Server Agent Jobs (automated)
│   └── entrypoint.sh                  # Script khởi tạo
├── start.sh                           # Script khởi động nhanh
└── README.md                          # Hướng dẫn này
```

## Quản lý database

### Kết nối với SQL Server Management Studio (SSMS)
1. Mở SSMS
2. Server name: localhost,1433
3. Authentication: SQL Server Authentication
4. Login: sa
5. Password: MilkTeaShop@2024

### Kết nối với Azure Data Studio
1. Mở Azure Data Studio
2. New Connection
3. Server: localhost,1433
4. Authentication type: SQL Login
5. User name: sa
6. Password: MilkTeaShop@2024

### Sử dụng sqlcmd (command line)
```bash
# Kết nối vào container (mssql-tools18)
docker exec -it milk-tea-shop-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P MilkTeaShop@2024 -C

# Hoặc từ host machine (nếu đã cài sqlcmd)
sqlcmd -S localhost,1433 -U sa -P MilkTeaShop@2024 -C
```

## Dừng và xóa

### Dừng containers
```bash
docker-compose down
```

### Xóa volumes (mất dữ liệu)
```bash
docker-compose down -v
```

### Xóa hoàn toàn
```bash
docker-compose down -v --rmi all
```

## Backup và Restore

### Backup
```sql
BACKUP DATABASE MilkTeaShop 
TO DISK = '/var/opt/mssql/backup/MilkTeaShop.bak'
WITH FORMAT, COMPRESSION;
```

### Restore
```sql
RESTORE DATABASE MilkTeaShop 
FROM DISK = '/var/opt/mssql/backup/MilkTeaShop.bak'
WITH REPLACE;
```

## SQL Server Agent Jobs

Database bao gồm các stored procedures để thay thế MySQL Event Scheduler:

### Procedures có sẵn

**System Maintenance:**
- `sp_daily_membership_maintenance` - Bảo trì membership hàng ngày
- `sp_daily_system_maintenance` - Bảo trì hệ thống tổng hợp
- `sp_deactivate_expired_discounts` - Vô hiệu hóa discount hết hạn
- `sp_cancel_overdue_orders` - Hủy order quá hạn

**Reporting System:**
- `sp_report_revenue_by_date_range` - Báo cáo doanh thu theo thời gian
- `sp_report_revenue_by_employee` - Báo cáo doanh thu theo nhân viên
- `sp_report_top_selling_products` - Báo cáo sản phẩm bán chạy
- `sp_report_revenue_by_category` - Báo cáo doanh thu theo danh mục
- `sp_report_vip_customers` - Báo cáo khách hàng VIP
- `sp_report_table_performance` - Báo cáo hiệu suất bàn
- `sp_report_discount_usage` - Báo cáo sử dụng discount

**User Management:**
- `sp_grant_permissions_by_role` - Cấp quyền theo role
- `sp_revoke_permissions` - Thu hồi quyền  
- `sp_lock_unlock_account` - Khóa/mở khóa tài khoản
- `sp_create_manager_account` - Tạo tài khoản manager
- `sp_create_staff_account` - Tạo tài khoản staff

### SQL Server Agent Jobs (Tự động)

Database đã được thiết lập với **SQL Server Agent enabled** và có job tự động thay thế MySQL Event:

**Job được tạo sẵn:**
1. **Deactivate Expired Discounts** - Chạy hàng ngày lúc 1:00 AM
   - Vô hiệu hóa các discount đã hết hạn
   - Thay thế cho MySQL event `event_deactivate_expired_discounts`

**Quản lý Job:**
```sql
-- Chạy job thủ công
EXEC msdb.dbo.sp_start_job @job_name = 'Deactivate Expired Discounts';

-- Tắt job tạm thời
UPDATE msdb.dbo.sysjobs SET enabled = 0 WHERE name = 'Deactivate Expired Discounts';

-- Xem lịch sử job
SELECT * FROM msdb.dbo.sysjobhistory WHERE job_id = 
  (SELECT job_id FROM msdb.dbo.sysjobs WHERE name = 'Deactivate Expired Discounts');

-- Liệt kê job
SELECT name, enabled, description FROM msdb.dbo.sysjobs 
WHERE name = 'Deactivate Expired Discounts';
```

**Chạy thủ công Procedures:**
```sql
-- Bảo trì hàng ngày
EXEC sp_daily_system_maintenance;

-- Bảo trì membership riêng
EXEC sp_daily_membership_maintenance;
```

## Troubleshooting

### Container không khởi động được
- Kiểm tra RAM: SQL Server cần ít nhất 2GB RAM
- Kiểm tra port 1433 có bị chiếm không: `netstat -an | grep 1433`

### Không kết nối được
- Kiểm tra firewall
- Đảm bảo password đúng định dạng (ít nhất 8 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt)

### Lỗi permission
```bash
# Nếu gặp lỗi permission với volumes
sudo chown -R 10001:0 /path/to/your/data/directory
```

## Ghi chú

- Database được tạo tự động khi container khởi động
- Tất cả dữ liệu được lưu trữ trong Docker volume `sqlserver_data`
- Encoding được thiết lập là `SQL_Latin1_General_CP1_CI_AS` để hỗ trợ tiếng Việt
- Các triggers bảo vệ dữ liệu mặc định đã được chuyển đổi hoàn toàn 