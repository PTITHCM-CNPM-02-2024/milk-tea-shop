# Stage 1: Build với Gradle
# Sử dụng base image chứa JDK (ví dụ: Temurin JDK 17)
FROM eclipse-temurin:17-jdk-jammy AS builder

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép các file cần thiết cho Gradle
COPY backend/gradlew .
COPY backend/gradlew.bat .
COPY backend/gradle ./gradle
COPY backend/build.gradle.kts .
COPY backend/settings.gradle.kts .
COPY backend/gradle.properties .

# Sao chép mã nguồn
# Lưu ý: Chỉ sao chép những gì cần thiết để tối ưu cache
COPY backend/src ./src
COPY backend/bootstrap ./bootstrap
COPY backend/api ./api
COPY backend/application ./application
COPY backend/domain ./domain
COPY backend/infrastructure ./infrastructure
COPY backend/platform ./platform
COPY backend/shared ./shared

# Cấp quyền thực thi cho gradlew
RUN chmod +x ./gradlew

# Build ứng dụng và tạo JAR
# Chạy clean để đảm bảo build sạch
RUN ./gradlew clean bootJar --no-daemon

# Stage 2: Tạo image cuối cùng
# Sử dụng base image chỉ chứa JRE
FROM eclipse-temurin:17-jre-jammy

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép JAR từ stage builder
# Đường dẫn JAR có thể cần điều chỉnh tùy thuộc vào cấu hình build.gradle.kts
COPY --from=builder /app/bootstrap/build/libs/*.jar app.jar

# Mở cổng ứng dụng (mặc định của Spring Boot là 8080)
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"] 