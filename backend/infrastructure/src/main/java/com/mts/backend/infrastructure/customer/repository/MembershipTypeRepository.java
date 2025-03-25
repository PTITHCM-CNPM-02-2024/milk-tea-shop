package com.mts.backend.infrastructure.customer.repository;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.infrastructure.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.infrastructure.persistence.entity.MembershipTypeEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MembershipTypeRepository implements IMembershipTypeRepository {
    private final JpaMembershipTypeRepository jpaMembershipTypeRepository;

    public MembershipTypeRepository(JpaMembershipTypeRepository jpaMembershipTypeRepository) {
        this.jpaMembershipTypeRepository = jpaMembershipTypeRepository;
    }

    /**
     * @param membershipTypeId
     * @return
     */
    @Override
    public Optional<MembershipType> findById(MembershipTypeId membershipTypeId) {
        
        Objects.requireNonNull(membershipTypeId, "Membership type id is required");

        return jpaMembershipTypeRepository.findById(membershipTypeId.getValue()).map(e -> {
            MemberDiscountValue memberDiscountValue = MemberDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit());

            return new MembershipType(
                    MembershipTypeId.of(e.getId()),
                    MemberTypeName.of(e.getType()),
                    memberDiscountValue,
                    e.getRequiredPoint(),
                    e.getDescription(),
                    e.getValidUntil(),
                    e.getIsActive(),
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            );

        });
    }

    /**
     * @param membershipType
     */
    @Override
    @Transactional
    public MembershipType save(MembershipType membershipType) {
        Objects.requireNonNull(membershipType, "Membership type is required");
        try {
            if (jpaMembershipTypeRepository.existsById(membershipType.getId().getValue())) {
                return update(membershipType);
            } else {
                return create(membershipType);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    protected MembershipType create(MembershipType membershipType) {
        Objects.requireNonNull(membershipType, "Membership type is required");

        MembershipTypeEntity mem = MembershipTypeEntity.builder()
                .id(membershipType.getId().getValue())
                .type(membershipType.getName().getValue())
                .discountValue(membershipType.getDiscountValue().getValue())
                .discountUnit(membershipType.getDiscountValue().getUnit())
                .requiredPoint(membershipType.getRequiredPoint())
                .description(membershipType.getDescription().orElse(null))
                .validUntil(membershipType.getValidUntil())
                .isActive(membershipType.isActive())
                .build();

        jpaMembershipTypeRepository.insertMembership(mem);

        return membershipType;
    }

    @Transactional
    protected MembershipType update(MembershipType membershipType) {
        Objects.requireNonNull(membershipType, "Membership type is required");

        MembershipTypeEntity mem = MembershipTypeEntity.builder()
                .id(membershipType.getId().getValue())
                .type(membershipType.getName().getValue())
                .discountValue(membershipType.getDiscountValue().getValue())
                .discountUnit(membershipType.getDiscountValue().getUnit())
                .requiredPoint(membershipType.getRequiredPoint())
                .description(membershipType.getDescription().orElse(null))
                .validUntil(membershipType.getValidUntil())
                .isActive(membershipType.isActive())
                .build();

        jpaMembershipTypeRepository.updateMembership(mem);

        return membershipType;
    }

    /**
     * @param membershipTypeId
     * @return
     */
    @Override
    public boolean existsById(MembershipTypeId membershipTypeId) {
        Objects.requireNonNull(membershipTypeId, "Membership type id is required");

        return jpaMembershipTypeRepository.existsById(membershipTypeId.getValue());
    }

    /**
     * @param name
     * @return
     */
    @Override
    public boolean existsByName(MemberTypeName name) {
        Objects.requireNonNull(name, "Name is required");

        return jpaMembershipTypeRepository.existsByType(name.getValue());
    }

    @Override
    public Optional<MembershipType> findByName(String name) {
        Objects.requireNonNull(name, "Name is required");

        return jpaMembershipTypeRepository.findByType(name).map(e -> {
            MemberDiscountValue memberDiscountValue = MemberDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit());

            return new MembershipType(
                    MembershipTypeId.of(e.getId()),
                    MemberTypeName.of(e.getType()),
                    memberDiscountValue,
                    e.getRequiredPoint(),
                    e.getDescription(),
                    e.getValidUntil(),
                    e.getIsActive(),
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            );

        });
    }
    
    @Override
    public Optional<MembershipType> findByDiscountValue(MemberDiscountValue memberDiscountValue) {
        Objects.requireNonNull(memberDiscountValue, "Discount value is required");

        var re = jpaMembershipTypeRepository.findDistinctByDiscountUnitAndDiscountValue(memberDiscountValue.getUnit(), memberDiscountValue.getValue());
        
        return re.stream().findFirst().map(e -> {
            MemberDiscountValue memberDiscountValue1 = MemberDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit());

            return new MembershipType(
                    MembershipTypeId.of(e.getId()),
                    MemberTypeName.of(e.getType()),
                    memberDiscountValue1,
                    e.getRequiredPoint(),
                    e.getDescription(),
                    e.getValidUntil(),
                    e.getIsActive(),
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            );

        });
    }
    @Override
    public List<MembershipType> findAll() {
        return jpaMembershipTypeRepository.findAll().stream()
                .map(e -> {
                    MemberDiscountValue memberDiscountValue = MemberDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit());

                    return new MembershipType(
                            MembershipTypeId.of(e.getId()),
                            MemberTypeName.of(e.getType()),
                            memberDiscountValue,
                            e.getRequiredPoint(),
                            e.getDescription(),
                            e.getValidUntil(),
                            e.getIsActive(),
                            e.getUpdatedAt().orElse(LocalDateTime.now())
                    );
                })
                .toList();
    }
}

