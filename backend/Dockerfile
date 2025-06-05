# Sử dụng một base image OpenJDK. Chọn phiên bản phù hợp với dự án của bạn.
# Ví dụ: Eclipse Temurin là một lựa chọn tốt, hoặc Amazon Corretto.
FROM eclipse-temurin:23

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR đã được build của ứng dụng Spring Boot vào container.
# Tên file JAR có thể khác tùy thuộc vào cấu hình build của bạn (ví dụ: target/your-app-name.jar).
# Thông thường, Spring Boot build ra file JAR trong thư mục target/
# Đổi 'your-application.jar' thành tên file JAR thực tế của bạn.
COPY build/libs/*.jar app.jar

# Cổng mà ứng dụng Spring Boot của bạn lắng nghe
EXPOSE 8181

# Lệnh để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]

# Bạn có thể thêm các biến môi trường tại đây nếu chúng không được quản lý bởi docker-compose
# ENV SPRING_PROFILES_ACTIVE=dev 