package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "InventoryTransaction", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "material_id", columnList = "material_id"),
        @Index(name = "supplier_id", columnList = "supplier_id"),
        @Index(name = "manager_id", columnList = "manager_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class InventoryTransaction extends BaseEntity<Long> {
    @Id
    @Comment("Mã giao dịch kho")
    @Column(name = "transaction_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nguyên vật liệu")
    @JoinColumn(name = "material_id")
    private com.ptithcm.infrastructure.persistence.entity.Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nhà cung cấp")
    @JoinColumn(name = "supplier_id")
    private com.ptithcm.infrastructure.persistence.entity.Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã quản lý")
    @JoinColumn(name = "manager_id")
    private com.ptithcm.infrastructure.persistence.entity.Manager manager;

    @Comment("Loại giao dịch (INBOUND, OUTBOUND)")
    @Lob
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Comment("Số lượng")
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Comment("Ngày giao dịch")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Comment("Đơn giá")
    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Comment("Số lô")
    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Comment("Ghi chú")
    @Lob
    @Column(name = "notes")
    private String notes;

}