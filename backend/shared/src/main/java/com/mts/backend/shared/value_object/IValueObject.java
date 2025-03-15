package com.mts.backend.shared.value_object;

/**
 * Marker interface for Value Objects in Domain-Driven Design.
 * <p>
 * Value Objects:
 * - Không có danh tính (identity) - được xác định bởi tất cả thuộc tính
 * - Bất biến (immutable) - không thể thay đổi sau khi tạo
 * - So sánh bằng giá trị (value equality) - hai ValueObjects bằng nhau nếu tất cả thuộc tính bằng nhau
 * - Không có side effects - hoạt động trên ValueObject luôn tạo instance mới
 * <p>
 * Mọi class implements interface này phải:
 * 1. Override equals() và hashCode() dựa trên tất cả thuộc tính
 * 2. Là immutable (tất cả fields là final)
 * 3. Tạo instance mới cho mọi thao tác thay đổi
 */
public interface IValueObject {

    /**
     * So sánh giá trị của đối tượng này với đối tượng khác.
     * Hai ValueObjects bằng nhau nếu tất cả thuộc tính của chúng bằng nhau.
     *
     * @param obj Đối tượng cần so sánh
     * @return true nếu đối tượng bằng nhau, false nếu không
     */
    @Override
    boolean equals(Object obj);

    /**
     * Tính toán mã băm dựa trên các thuộc tính của đối tượng.
     *
     * @return Mã băm của đối tượng
     */
    @Override
    int hashCode();
    
    
}
