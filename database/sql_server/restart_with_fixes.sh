#!/bin/bash
# Script restart database với tất cả các fixes

echo "🔄 Restarting SQL Server với tất cả fixes..."

# 1. Stop container hiện tại
echo "📤 Đang dừng container..."
docker-compose down

# 2. Rebuild và start lại
echo "🚀 Đang rebuild và start container..."
docker-compose up --build -d

# 3. Đợi SQL Server khởi động
echo "⏳ Đợi SQL Server khởi động (45 giây)..."
sleep 45

# 4. Kiểm tra container status
echo "📊 Kiểm tra trạng thái container..."
docker-compose ps

# 5. Xem logs
echo "📝 Xem logs khởi tạo database..."
docker-compose logs milk-tea-shop-sqlserver | tail -20

echo "✅ Hoàn thành restart với fixes!"
echo ""
echo "🔗 Để kiểm tra database:"
echo "docker exec -it milk-tea-shop-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P MilkTeaShop@2024 -C" 