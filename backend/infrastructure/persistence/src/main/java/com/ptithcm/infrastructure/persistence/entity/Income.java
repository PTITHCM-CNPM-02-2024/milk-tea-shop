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
@Table(name = "Income", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "account_id", columnList = "account_id"),
        @Index(name = "income_category_id", columnList = "income_category_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Income extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã thu nhập")
    @Column(name = "income_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã loại thu nhập")
    @JoinColumn(name = "income_category_id")
    private com.ptithcm.infrastructure.persistence.entity.IncomeCategory incomeCategory;

    @Comment("Số tiền")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Comment("Ngày giao dịch")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

}