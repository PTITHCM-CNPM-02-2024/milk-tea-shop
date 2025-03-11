package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "RewardPointLog", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class RewardPointLog extends BaseEntity <Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã lịch sử điểm thưởng")
    @Column(name = "reward_point_log_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Comment("Số điểm thưởng")
    @Column(name = "reward_point", nullable = false)
    private Integer rewardPoint;

}