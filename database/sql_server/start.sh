#!/bin/bash
# Script khởi chạy nhanh SQL Server 2022 cho Milk Tea Shop

echo "🚀 Đang khởi động SQL Server 2022 Database cho Milk Tea Shop..."
echo "=================================================="

# Kiểm tra Docker đã được cài đặt chưa
if ! command -v docker &> /dev/null; then
    echo "❌ Docker chưa được cài đặt. Vui lòng cài Docker trước."
    exit 1
fi

# Kiểm tra Docker Compose đã được cài đặt chưa
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose chưa được cài đặt. Vui lòng cài Docker Compose trước."
    exit 1
fi

# Tạo thư mục nếu chưa có
mkdir -p ../init/dev/sqlserver

# Cấp quyền thực thi cho script entrypoint
chmod +x ../init/dev/sqlserver/entrypoint.sh

echo "📋 Thông tin kết nối:"
echo "   - Server: localhost,1433"
echo "   - Username: sa"
echo "   - Password: MilkTeaShop@2024"
echo "   - Database: MilkTeaShop"
echo ""

echo "🔧 Khởi động containers..."
docker-compose up -d

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ SQL Server đã khởi động thành công!"
    echo ""
    echo "📊 Kiểm tra trạng thái:"
    docker-compose ps
    echo ""
    echo "📝 Để xem logs:"
    echo "   docker-compose logs -f sqlserver"
    echo ""
    echo "🔗 Chuỗi kết nối:"
    echo "   Server=localhost,1433;Database=MilkTeaShop;User Id=sa;Password=MilkTeaShop@2024;TrustServerCertificate=true;"
    echo ""
    echo "🛑 Để dừng:"
    echo "   docker-compose down"
else
    echo "❌ Có lỗi xảy ra khi khởi động containers."
    exit 1
fi 