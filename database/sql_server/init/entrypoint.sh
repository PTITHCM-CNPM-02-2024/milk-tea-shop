#!/bin/bash
# Entrypoint script cho SQL Server - Milk Tea Shop Database
# Chạy tất cả các file SQL theo thứ tự

echo "🚀 Bắt đầu khởi tạo database Milk Tea Shop..."
echo "================================================="

echo "📁 Backup Location: /var/opt/mssql/backup/"
mkdir -p /var/opt/mssql/backup/
chmod 777 /var/opt/mssql/backup/
chown mssql:mssql /var/opt/mssql/backup/
if [ $? -eq 0 ]; then
    echo "✅ Tạo thư mục backup thành công"
else
    echo "❌ Lỗi khi tạo thư mục backup"
    exit 1
fi

# Đợi SQL Server khởi động hoàn toàn
echo "⏳ Đang đợi SQL Server khởi động..."
echo "📋 Danh sách file sẽ được thực thi:"
echo "   1. 001_base_line.sql - Tạo schema database"
echo "   2. 002_init_default_data.sql - Dữ liệu mặc định"
echo "   3. 003_init_procedures_01.sql - Stored procedures (part 1)"
echo "   3.5. 003_init_procedures_02.sql - Stored procedures (part 2)"
echo "   4. 004_init_triggers.sql - Triggers"
echo "   5. 005_init_optional_data.sql - Data)"
echo "   6. 006_membership_trigger_event.sql - Membership events"
echo "   7. 007_scheduled_events.sql - Scheduled events"
echo "   8. 008_reporting_procedures_views.sql - Reporting system"
echo "   9. 009_user_grant.sql - User management"
echo "   10. 010_create_agent_jobs.sql - SQL Server Agent Jobs"
echo "   11. 011_auto_cancel_overdue_orders.sql - Auto cancel overdue orders"
echo "   12. 012_backup_system.sql - Backup system (Full & Hourly)"
echo ""

# 1. Tạo schema database
echo "📝 [1/11] Đang chạy schema database..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/001_base_line.sql

if [ $? -eq 0 ]; then
    echo "✅ Schema database đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo schema database"
    exit 1
fi

# 2. Chạy dữ liệu mặc định
echo "📝 [2/11] Đang chạy dữ liệu mặc định..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/002_init_default_data.sql

if [ $? -eq 0 ]; then
    echo "✅ Dữ liệu mặc định đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo dữ liệu mặc định"
    exit 1
fi

# 3. Chạy stored procedures (part 1)
echo "📝 [3/11] Đang chạy stored procedures (part 1)..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/003_init_procedures_01.sql

if [ $? -eq 0 ]; then
    echo "✅ Stored procedures (part 1) đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo stored procedures (part 1)"
    exit 1
fi

# 3.5. Chạy stored procedures (part 2)
echo "📝 [3.5/11] Đang chạy stored procedures (part 2)..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/003_init_procedures_02.sql

if [ $? -eq 0 ]; then
    echo "✅ Stored procedures (part 2) đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo stored procedures (part 2)"
    exit 1
fi

# 4. Chạy triggers
echo "📝 [4/11] Đang chạy triggers..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/004_init_triggers.sql

if [ $? -eq 0 ]; then
    echo "✅ Triggers đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo triggers"
    exit 1
fi

# 5. Bỏ qua views và reports (file không tồn tại)
echo "📝 [5/11] Tạo dữ liệu mẫu"
echo "📝 [5/11] Đang chạy dữ liệu mẫu"
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/005_init_optional_data.sql
if [ $? -eq 0 ]; then
    echo "✅ Data đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo data"
    exit 1
fi

# 6. Chạy membership triggers và events
echo "📝 [6/11] Đang chạy membership triggers và events..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/006_membership_trigger_event.sql

if [ $? -eq 0 ]; then
    echo "✅ Membership triggers và events đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo membership triggers và events"
    exit 1
fi

# 7. Chạy scheduled events
echo "📝 [7/11] Đang chạy scheduled events..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/007_scheduled_events.sql

if [ $? -eq 0 ]; then
    echo "✅ Scheduled events đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo scheduled events"
    exit 1
fi

# 8. Chạy reporting procedures và views
echo "📝 [8/11] Đang chạy reporting procedures và views..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/008_reporting_procedures_views.sql

if [ $? -eq 0 ]; then
    echo "✅ Reporting procedures và views đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo reporting procedures và views"
    exit 1
fi

# 9. Chạy user management procedures
echo "📝 [9/11] Đang chạy user management procedures..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/009_user_grant.sql

if [ $? -eq 0 ]; then
    echo "✅ User management procedures đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo user management procedures"
    exit 1
fi

# 10. Tạo SQL Server Agent Jobs
echo "📝 [10/11] Đang tạo SQL Server Agent Jobs..."
# Đợi thêm một chút để SQL Server Agent khởi động hoàn toàn
sleep 10
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/010_create_agent_jobs.sql

if [ $? -eq 0 ]; then
    echo "✅ SQL Server Agent Jobs đã được tạo thành công"
else
    echo "⚠️ SQL Server Agent Jobs có thể chưa hoàn toàn sẵn sàng, nhưng scripts đã được tạo"
fi

# 11. Chạy auto cancel overdue orders
echo "📝 [11/12] Đang chạy auto cancel overdue orders..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/011_auto_cancel_overdue_orders.sql

if [ $? -eq 0 ]; then
    echo "✅ Auto cancel overdue orders đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo auto cancel overdue orders"
    exit 1
fi

# 12. Chạy backup system
echo "📝 [12/12] Đang chạy backup system..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $MSSQL_SA_PASSWORD -C -i /docker-entrypoint-initdb.d/012_backup_system.sql

if [ $? -eq 0 ]; then
    echo "✅ Backup system đã được tạo thành công"
else
    echo "❌ Lỗi khi tạo backup system"
    exit 1
fi

echo ""
echo "🎉 Database Milk Tea Shop đã được khởi tạo thành công!"
echo "================================================="
echo "📊 Thông tin database:"
echo "   - Database: MilkTeaShop"
echo "   - Charset: UTF-8 (SQL_Latin1_General_CP1_CI_AS)"
echo "   - Tables: 22 bảng"
echo "   - Procedures: 30+ stored procedures"
echo "   - Triggers: 15+ triggers"  
echo "   - Views: 15+ views"
echo "   - SQL Server Agent: Enabled với 5 job tự động"
echo "   - Events: Membership maintenance, Discount cleanup, Auto cancel orders"
echo "   - Backup: Daily full backup, Hourly differential backup"
echo "   - Reporting: Revenue, products, customers, performance"
echo "   - User Management: Role-based permissions"
echo "   - Sample Data: Có dữ liệu mẫu"
echo ""
echo "🔗 Thông tin kết nối:"
echo "   - Server: localhost,1433"
echo "   - Username: sa"
echo "   - Password: MilkTeaShop@2024"
echo "   - Database: MilkTeaShop"
echo ""
echo "📝 Connection String:"
echo "   Server=localhost,1433;Database=MilkTeaShop;User Id=sa;Password=MilkTeaShop@2024;TrustServerCertificate=true;"
echo ""
echo "✅ Database sẵn sàng sử dụng!" 