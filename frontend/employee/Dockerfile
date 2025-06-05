# Giai đoạn 1: Build ứng dụng Frontend
# Sử dụng Node.js image, chọn phiên bản phù hợp với dự án của bạn
FROM node:18-alpine AS builder

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép package.json và package-lock.json (hoặc yarn.lock)
COPY package*.json ./
# Nếu dùng yarn, hãy dùng:
# COPY package.json yarn.lock ./

# Cài đặt dependencies
RUN npm install
# Nếu dùng yarn, hãy dùng:
# RUN yarn install

# Sao chép toàn bộ mã nguồn của frontend vào container
COPY . .

# Build ứng dụng
# Cung cấp biến môi trường API_URL trong quá trình build nếu cần
# ARG API_URL
# ENV VITE_API_URL=${API_URL} # Hoặc REACT_APP_API_URL=${API_URL}
RUN npm run build
# Nếu dùng yarn, hãy dùng:
# RUN yarn build

# Giai đoạn 2: Phục vụ ứng dụng với Node.js và serve
FROM node:22-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Cài đặt serve trên toàn cục
RUN npm install -g serve

# Sao chép các tệp đã build từ giai đoạn builder
# Thư mục chứa kết quả build thường là 'dist' hoặc 'build'
# Hãy đảm bảo đường dẫn này khớp với cấu hình build của bạn
COPY --from=builder /app/dist .

# Cổng mà serve sẽ lắng nghe (serve mặc định là 3000, nhưng chúng ta muốn 80 để khớp với docker-compose)
EXPOSE 80

# Lệnh để chạy ứng dụng
# `serve -s . -l 80` sẽ phục vụ thư mục hiện tại và lắng nghe trên cổng 80
CMD ["serve", "-s", ".", "-l", "80"]

# --- Ghi chú về nginx.conf (Tùy chọn) ---
# Nếu bạn cần cấu hình Nginx phức tạp hơn (ví dụ: routing, fallback cho SPA), 
# bạn có thể tạo một tệp `nginx.conf` trong thư mục `frontend/employee` và sao chép nó vào.
# Ví dụ nội dung `nginx.conf` cho một SPA:
# server {
#   listen 80;
#   server_name localhost;
# 
#   root /usr/share/nginx/html;
#   index index.html index.htm;
# 
#   location / {
#     try_files $uri $uri/ /index.html;
#   }
# 
#   # Proxy API requests to backend (nếu frontend gọi API qua Nginx proxy thay vì trực tiếp từ client)
#   # location /api {
#   #   proxy_pass http://backend_service_name:backend_port; # Thay thế bằng tên và cổng dịch vụ backend
#   #   proxy_set_header Host $host;
#   #   proxy_set_header X-Real-IP $remote_addr;
#   #   proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
#   #   proxy_set_header X-Forwarded-Proto $scheme;
#   # }
# } 