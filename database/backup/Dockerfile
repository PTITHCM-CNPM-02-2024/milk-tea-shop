# Sử dụng image Percona XtraBackup chính thức
FROM percona/percona-xtrabackup:8.0

# Đặt người dùng là root để có đủ quyền trong container (ví dụ: ghi vào mounted volumes)
USER root

WORKDIR /usr/local/bin

# Sao chép script backup và restore vào container, cấp quyền thực thi
COPY --chmod=+x backup.sh backup.sh
# COPY --chmod=+x restore.sh restore.sh

# Khai báo volume để lưu trữ các bản backup từ host
VOLUME ["/mnt/backups"]

# Đặt thư mục làm việc cho script backup
WORKDIR /mnt/backups

# Chạy script backup khi container khởi động
# Script backup.sh sẽ là một vòng lặp dài, thực hiện backup định kỳ
CMD ["/usr/local/bin/backup.sh"] 