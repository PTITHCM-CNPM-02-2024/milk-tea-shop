#!/bin/bash
set -eo pipefail

BACKUP_DIR_IN_CONTAINER="${BACKUP_DIR_IN_CONTAINER:-/mnt/backups}"
RESTORE_PREPARED_DIR="${RESTORE_PREPARED_DIR:-/mnt/backups/restore_prepared_data}"
NO_DEFAULTS_FLAG="--no-defaults"

echof() {
    echo "INFO: [$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

errorf() {
    echo "ERROR: [$(date '+%Y-%m-%d %H:%M:%S')] $1" >&2
}

cd "${BACKUP_DIR_IN_CONTAINER}" || {
  errorf "Không thể truy cập thư mục backup ${BACKUP_DIR_IN_CONTAINER}"
  exit 1
}

echof "Các full backups có sẵn để restore:"
ls -td -- *--full 2>/dev/null | nl

read -rp "Nhập số thứ tự của FULL backup bạn muốn restore (ví dụ: 1): " choice

selected_full_backup_basename=$(ls -td -- *--full 2>/dev/null | sed -n "${choice}p")

if [ -z "${selected_full_backup_basename}" ] || ! [ -d "${selected_full_backup_basename}" ]; then
    errorf "Lựa chọn không hợp lệ hoặc không tìm thấy thư mục full backup: '${selected_full_backup_basename}'"
    exit 1
fi

full_backup_to_restore_path="${BACKUP_DIR_IN_CONTAINER}/${selected_full_backup_basename}"
echof "Bạn đã chọn restore từ full backup: ${full_backup_to_restore_path}"

# Tạo thư mục tạm để chuẩn bị dữ liệu restore
rm -rf "${RESTORE_PREPARED_DIR}"
mkdir -p "${RESTORE_PREPARED_DIR}"
echof "Đang sao chép full backup ${selected_full_backup_basename} vào ${RESTORE_PREPARED_DIR} để chuẩn bị..."
cp -a "${full_backup_to_restore_path}/." "${RESTORE_PREPARED_DIR}/"

# Chuẩn bị (redo-only) bản full backup đã chọn trong thư mục tạm
echof "Chuẩn bị (redo-only) bản full backup tại ${RESTORE_PREPARED_DIR}..."
xtrabackup --prepare --apply-log-only ${NO_DEFAULTS_FLAG} --target-dir="${RESTORE_PREPARED_DIR}"
if [ $? -ne 0 ]; then
    errorf "Lỗi khi chuẩn bị (redo-only) full backup tại ${RESTORE_PREPARED_DIR}."
    exit 1
fi
echof "Full backup đã được chuẩn bị (redo-only)."

# Tìm và áp dụng các incremental backups liên quan theo thứ tự thời gian
# Các incremental backups phải được tạo SAU full backup này và TRƯỚC full backup tiếp theo (nếu có)
echof "Đang tìm và áp dụng các incremental backups liên quan..."

# Lấy timestamp của full backup đã chọn
full_backup_timestamp_str=$(echo "${selected_full_backup_basename}" | cut -d'-' -f1)

# Tìm full backup tiếp theo (nếu có) để giới hạn các incrementals
next_full_backup_basename=$(ls -td -- *--full 2>/dev/null | grep -B1 "${selected_full_backup_basename}" | head -n1 | grep -v "${selected_full_backup_basename}" || true)
next_full_backup_timestamp_str=""
if [ -n "${next_full_backup_basename}" ]; then
    next_full_backup_timestamp_str=$(echo "${next_full_backup_basename}" | cut -d'-' -f1)
fi 
echof "Full backup gốc: ${full_backup_timestamp_str}. Full backup kế tiếp (nếu có): ${next_full_backup_timestamp_str}"

applied_incrementals=0
for inc_backup_basename in $(ls -t -- *--inc 2>/dev/null | tac); # Sắp xếp từ cũ nhất đến mới nhất
do
    inc_backup_path="${BACKUP_DIR_IN_CONTAINER}/${inc_backup_basename}"
    inc_backup_timestamp_str=$(echo "${inc_backup_basename}" | cut -d'-' -f1)

    # Kiểm tra xem incremental này có thuộc về full backup đã chọn không
    if [ -f "${inc_backup_path}/xtrabackup_checkpoints" ]; then
        basedir_info=$(grep 'incremental_basedir' "${inc_backup_path}/xtrabackup_checkpoints" | awk -F'=' '{print $2}' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
        basedir_name=$(basename "${basedir_info}")
        
        if [ "${basedir_name}" == "${selected_full_backup_basename}" ]; then
            # Kiểm tra timestamp của incremental nằm trong khoảng hợp lệ
            # (Không cần thiết nếu đã kiểm tra basedir, nhưng để chắc chắn)
            if [[ "${inc_backup_timestamp_str}" > "${full_backup_timestamp_str}" ]] && 
               ([[ -z "${next_full_backup_timestamp_str}" ]] || [[ "${inc_backup_timestamp_str}" < "${next_full_backup_timestamp_str}" ]]); then
                
                echof "Đang áp dụng incremental backup: ${inc_backup_path} vào ${RESTORE_PREPARED_DIR}"
                xtrabackup --prepare --apply-log-only ${NO_DEFAULTS_FLAG} \
                           --target-dir="${RESTORE_PREPARED_DIR}" \
                           --incremental-dir="${inc_backup_path}"
                if [ $? -ne 0 ]; then
                    errorf "Lỗi khi áp dụng incremental ${inc_backup_path} vào ${RESTORE_PREPARED_DIR}."
                    exit 1
                fi
                applied_incrementals=$((applied_incrementals + 1))
            fi
        fi
    fi
done

echof "Đã áp dụng ${applied_incrementals} incremental backup(s)."

# Chạy prepare cuối cùng trên thư mục tạm (không có --apply-log-only)
echof "Chạy prepare cuối cùng trên ${RESTORE_PREPARED_DIR} để sẵn sàng cho restore..."
xtrabackup --prepare ${NO_DEFAULTS_FLAG} --target-dir="${RESTORE_PREPARED_DIR}"
if [ $? -ne 0 ]; then
    errorf "Lỗi khi chạy prepare cuối cùng trên ${RESTORE_PREPARED_DIR}."
    exit 1
fi

echof "-----------------------------------------------------------------"
echof "Dữ liệu đã được chuẩn bị và sẵn sàng để restore tại: ${RESTORE_PREPARED_DIR}"
echof "Để restore, bạn cần dừng PXC node, xoá nội dung thư mục data của nó,"
echof "sau đó sao chép toàn bộ nội dung từ ${RESTORE_PREPARED_DIR} vào thư mục data đó."
echof "Ví dụ lệnh (chạy trên host, sau khi đã copy dữ liệu từ volume nếu cần):"
echof "  # docker cp <container_id_backup>:${RESTORE_PREPARED_DIR} /tmp/restore_data"
nechof "  # sudo cp -a /tmp/restore_data/. /đường/dẫn/tới/pxc_node_data_volume/"
echof "Sau đó khởi động lại PXC node và kiểm tra."
echof "-----------------------------------------------------------------"

exit 0 