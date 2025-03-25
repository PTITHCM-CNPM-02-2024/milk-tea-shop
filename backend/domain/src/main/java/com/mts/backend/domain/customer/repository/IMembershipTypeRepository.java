package com.mts.backend.domain.customer.repository;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;

import java.util.List;
import java.util.Optional;

public interface IMembershipTypeRepository {
    Optional<MembershipType> findById(MembershipTypeId membershipTypeId);
    MembershipType save(MembershipType membershipType);
    
    boolean existsById(MembershipTypeId membershipTypeId);
    
    boolean existsByName(MemberTypeName name);


    Optional<MembershipType> findByName(String name);

    Optional<MembershipType> findByDiscountValue(MemberDiscountValue memberDiscountValue);

    List<MembershipType> findAll();
}
