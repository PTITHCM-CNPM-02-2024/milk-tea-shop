#!/bin/bash
set -eo pipefail

# Thiết lập biến môi trường với giá trị mặc định
BACKUP_DIR_IN_CONTAINER="${BACKUP_DIR_IN_CONTAINER:-/mnt/backups}" # Thư mục lưu backup trong container
BACKUP_EVERY_SECONDS="${BACKUP_EVERY_SECONDS:-3600}" # Tần suất backup (giây), mặc định: 1 giờ
FULL_BACKUP_INTERVAL_COUNT="${FULL_BACKUP_INTERVAL_COUNT:-7}" # Số backup (full/inc) trước khi có full backup mới
KEEP_FULL_BACKUPS_COUNT="${KEEP_FULL_BACKUPS_COUNT:-3}"    # Số full backup sets giữ lại

MYSQL_HOST="${MYSQL_HOST:-milk_tea_pxc1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
# MYSQL_PASSWORD sẽ được cung cấp qua biến môi trường từ docker-compose

NO_DEFAULTS_FLAG="--no-defaults" # Thêm cờ --no-defaults để tránh đọc file my.cnf cục bộ

# Kiểm tra mật khẩu MySQL
if [ -z "${MYSQL_PASSWORD}" ]; then
  echo "ERROR: Biến môi trường MYSQL_PASSWORD chưa được thiết lập." >&2
  exit 1
fi

# Chuyển đến thư mục backup
cd "${BACKUP_DIR_IN_CONTAINER}" || {
  echo "ERROR: Không thể truy cập thư mục backup ${BACKUP_DIR_IN_CONTAINER}" >&2
  exit 1
}
echo "INFO: Thư mục làm việc cho backup: $(pwd)"

# Đếm số backup đã thực hiện kể từ full backup cuối cùng
# Khởi tạo bằng FULL_BACKUP_INTERVAL_COUNT để đảm bảo full backup đầu tiên được thực hiện
current_interval_count=${FULL_BACKUP_INTERVAL_COUNT}

echof() {
    echo "INFO: [$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

errorf() {
    echo "ERROR: [$(date '+%Y-%m-%d %H:%M:%S')] $1" >&2
}

cleanup_old_backups() {
    echof "Bắt đầu kiểm tra và xoá các backup cũ."
    all_full_backups=$(ls -td -- *--full 2>/dev/null)
    num_full_backups=$(echo "${all_full_backups}" | grep -c . || true) # grep -c . để đếm dòng không rỗng

    echof "Tìm thấy ${num_full_backups} full backup(s). Giữ lại ${KEEP_FULL_BACKUPS_COUNT} full backup set(s)."

    if [ "${num_full_backups}" -gt "${KEEP_FULL_BACKUPS_COUNT}" ]; then
        full_backups_to_delete=$(echo "${all_full_backups}" | tail -n +$((${KEEP_FULL_BACKUPS_COUNT} + 1)))
        
        for old_full_path_basename in ${full_backups_to_delete};
        do  
            # old_full_path_basename chỉ là tên thư mục, không phải đường dẫn đầy đủ
            old_full_path="${BACKUP_DIR_IN_CONTAINER}/${old_full_path_basename}"
            echof "Đang xử lý xoá full backup cũ: ${old_full_path}"

            # Tìm và xóa các incremental backups dựa trên full backup này
            # Duyệt qua tất cả các thư mục incremental
            for inc_backup_path_basename in $(ls -d -- *--inc 2>/dev/null);
            do
                inc_backup_path="${BACKUP_DIR_IN_CONTAINER}/${inc_backup_path_basename}"
                # Kiểm tra file xtrabackup_checkpoints để tìm incremental_basedir
                if [ -f "${inc_backup_path}/xtrabackup_checkpoints" ]; then
                    basedir_info=$(grep 'incremental_basedir' "${inc_backup_path}/xtrabackup_checkpoints" | awk -F'=' '{print $2}' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
                    # basedir_info có dạng path/to/basedir_timestamp--full
                    # Chúng ta cần lấy phần tên thư mục cuối cùng
                    basedir_name=$(basename "${basedir_info}")
                    if [ "${basedir_name}" == "${old_full_path_basename}" ]; then
                        echof "Đang xoá incremental backup liên quan: ${inc_backup_path}"
                        rm -rf "${inc_backup_path}"
                    fi
                fi
            done
            
            echof "Đang xoá thư mục full backup: ${old_full_path}"
            rm -rf "${old_full_path}"
        done
    else
        echof "Không có full backup cũ nào cần xoá."
    fi
}


# Vòng lặp backup chính
while true; do
  timestamp=$(date +%Y%m%d_%H%M%S)
  
  # Tìm full backup gần nhất
  latest_full_backup_basename=$( (ls -td -- *--full 2>/dev/null | head -n 1) || true )
  latest_full_backup_path="${BACKUP_DIR_IN_CONTAINER}/${latest_full_backup_basename}"

  is_full_backup=false
  if [ "${current_interval_count}" -ge "${FULL_BACKUP_INTERVAL_COUNT}" ] || [ -z "${latest_full_backup_basename}" ] || ! [ -d "${latest_full_backup_path}" ]; then
    is_full_backup=true
  fi

  target_backup_path=""
  backup_type=""

  if ${is_full_backup}; then
    backup_type="Full"
    target_backup_path="${BACKUP_DIR_IN_CONTAINER}/${timestamp}--full"
    echof "Bắt đầu ${backup_type} Backup -> ${target_backup_path}"
    xtrabackup --backup \
               ${NO_DEFAULTS_FLAG} \
               --host="${MYSQL_HOST}" --port="${MYSQL_PORT}" \
               --user="${MYSQL_USER}" --password="${MYSQL_PASSWORD}" \
               --target-dir="${target_backup_path}"
    backup_exit_code=$?
  else
    backup_type="Incremental"
    target_backup_path="${BACKUP_DIR_IN_CONTAINER}/${timestamp}--inc"
    echof "Bắt đầu ${backup_type} Backup (base: ${latest_full_backup_path}) -> ${target_backup_path}"
    xtrabackup --backup \
               ${NO_DEFAULTS_FLAG} \
               --host="${MYSQL_HOST}" --port="${MYSQL_PORT}" \
               --user="${MYSQL_USER}" --password="${MYSQL_PASSWORD}" \
               --target-dir="${target_backup_path}" \
               --incremental-basedir="${latest_full_backup_path}"
    backup_exit_code=$?
  fi

  if [ ${backup_exit_code} -eq 0 ]; then
    echof "${backup_type} Backup hoàn thành. Bắt đầu chuẩn bị (prepare)..."
    prepare_exit_code=0
    if ${is_full_backup}; then
      xtrabackup --prepare --apply-log-only ${NO_DEFAULTS_FLAG} --target-dir="${target_backup_path}"
      prepare_exit_code=$?
    else
      # Chuẩn bị incremental: áp dụng vào bản full nó dựa trên
      # Lưu ý: điều này sẽ thay đổi thư mục full backup gốc.
      xtrabackup --prepare --apply-log-only ${NO_DEFAULTS_FLAG} \
                 --target-dir="${latest_full_backup_path}" \
                 --incremental-dir="${target_backup_path}"
      prepare_exit_code=$?
    fi

    if [ ${prepare_exit_code} -eq 0 ]; then
      echof "Chuẩn bị ${backup_type} Backup thành công cho ${target_backup_path}."
      if ${is_full_backup}; then
        current_interval_count=1 # Reset sau full backup
      else
        current_interval_count=$((current_interval_count + 1))
      fi
      # Xoá thư mục incremental sau khi đã apply thành công (tùy chọn)
      # if ! ${is_full_backup}; then
      #   echof "Đang xoá thư mục incremental đã được áp dụng: ${target_backup_path}"
      #   rm -rf "${target_backup_path}"
      # fi
    else
      errorf "Chuẩn bị ${backup_type} Backup thất bại. Mã lỗi: ${prepare_exit_code}. Backup tại: ${target_backup_path}"
      # Không xóa target_backup_path để có thể debug
    fi
  else
    errorf "${backup_type} Backup thất bại. Mã lỗi: ${backup_exit_code}. Thư mục dự kiến: ${target_backup_path}"
    # Nếu thư mục được tạo, xóa nó đi
    if [ -d "${target_backup_path}" ]; then
        rm -rf "${target_backup_path}"
    fi
  fi

  # Dọn dẹp backup cũ
  cleanup_old_backups
  
  echof "Hoàn tất chu kỳ. Nghỉ ${BACKUP_EVERY_SECONDS} giây."
  sleep "${BACKUP_EVERY_SECONDS}"
done 