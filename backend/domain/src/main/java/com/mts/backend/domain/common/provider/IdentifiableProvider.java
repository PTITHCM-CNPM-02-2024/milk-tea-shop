package com.mts.backend.domain.common.provider;

import com.mts.backend.domain.promotion.identifier.CouponId;

import java.security.SecureRandom;

public class IdentifiableProvider {

    private static final SecureRandom secureRandom = new SecureRandom();

    // Giá trị tối đa cho các kiểu dữ liệu MySQL
    public static final int TINYINT_UNSIGNED_MAX = 255;
    public static final int SMALLINT_UNSIGNED_MAX = 65535;        // 2^16 - 1
    public static final int MEDIUMINT_UNSIGNED_MAX = 16777215;    // 2^24 - 1
    public static final long INT_UNSIGNED_MAX = 4294967295L;      // 2^32 - 1

    /**
     * Tạo số ngẫu nhiên kiểu SMALLINT UNSIGNED (0-65535)
     */
    public static int generateUnsignedSmallInt() {
        return secureRandom.nextInt(SMALLINT_UNSIGNED_MAX + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu SMALLINT UNSIGNED trong khoảng chỉ định
     */
    public static int generateUnsignedSmallInt(int min, int max) {
        if (min < 0) min = 0;
        if (max > SMALLINT_UNSIGNED_MAX) max = SMALLINT_UNSIGNED_MAX;
        return min + secureRandom.nextInt(max - min + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu MEDIUMINT UNSIGNED (0-16777215)
     */
    public static int generateUnsignedMediumInt() {
        return secureRandom.nextInt(MEDIUMINT_UNSIGNED_MAX + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu MEDIUMINT UNSIGNED trong khoảng chỉ định
     */
    public static int generateUnsignedMediumInt(int min, int max) {
        if (min < 0) min = 0;
        if (max > MEDIUMINT_UNSIGNED_MAX) max = MEDIUMINT_UNSIGNED_MAX;
        return min + secureRandom.nextInt(max - min + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu INT UNSIGNED (0-4294967295)
     * Lưu ý: Java không có kiểu unsigned, nên phải dùng long để chứa
     */
    public static long generateUnsignedInt() {
        // Phương pháp 1: Sử dụng 2 số int ngẫu nhiên
        int high = secureRandom.nextInt(65536); // 2^16
        int low = secureRandom.nextInt(65536);  // 2^16
        return ((long) high << 16) | (low & 0xFFFFL);

        // Phương pháp 2: Dùng nextLong và giới hạn phạm vi
        // return Math.abs(secureRandom.nextLong()) % (INT_UNSIGNED_MAX + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu INT UNSIGNED trong khoảng chỉ định
     */
    public static long generateUnsignedInt(long min, long max) {
        if (min < 0) min = 0;
        if (max > INT_UNSIGNED_MAX) max = INT_UNSIGNED_MAX;

        // Phương pháp an toàn cho khoảng rộng
        long range = max - min + 1;
        long bits, val;
        do {
            bits = (secureRandom.nextLong() << 1) >>> 1; // Loại bỏ bit dấu
            val = bits % range;
        } while (bits - val + (range - 1) < 0); // Để tránh bias

        return min + val;
    }

    /**
     * Tạo ID ngẫu nhiên dựa trên thời gian kết hợp với phần ngẫu nhiên
     * Hữu ích khi cần ID có tính duy nhất cao
     */
    public static int generateTimeBasedUnsignedSmallInt() {
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(1024); // 2^10
        return ((int) (timestamp & 0x3FFFF) ^ random) & SMALLINT_UNSIGNED_MAX;
    }

    public static int generateTimeBasedUnsignedMediumInt() {
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(4096); // 2^12
        return ((int) (timestamp & 0xFFFFF) ^ random) & MEDIUMINT_UNSIGNED_MAX;
    }

    public static long generateTimeBasedUnsignedInt() {
        long timestamp = System.currentTimeMillis();
        long random = secureRandom.nextInt(65536); // 2^16
        return ((timestamp) ^ random) & INT_UNSIGNED_MAX;
    }


    /**
     * Tạo số ngẫu nhiên kiểu TINYINT UNSIGNED (0-255)
     */
    public static int generateUnsignedTinyInt() {
        return secureRandom.nextInt(TINYINT_UNSIGNED_MAX + 1);
    }

    /**
     * Tạo số ngẫu nhiên kiểu TINYINT UNSIGNED trong khoảng chỉ định
     */
    public static int generateUnsignedTinyInt(int min, int max) {
        if (min < 0) min = 0;
        if (max > TINYINT_UNSIGNED_MAX) max = TINYINT_UNSIGNED_MAX;
        return min + secureRandom.nextInt(max - min + 1);
    }

    /**
     * Tạo ID ngẫu nhiên dựa trên thời gian cho TINYINT UNSIGNED
     * Kết hợp timestamp với số ngẫu nhiên
     */
    public static int generateTimeBasedUnsignedTinyInt() {
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(16); // 2^4
        return ((int)(timestamp & 0xFF) ^ random) & TINYINT_UNSIGNED_MAX;
    }
    
}
