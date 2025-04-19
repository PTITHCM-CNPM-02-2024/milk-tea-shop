package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateMembershipCommandHandler implements ICommandHandler<CreateMembershipCommand, CommandResult> {
    private final JpaMembershipTypeRepository membershipTypeRepository;

    public CreateMembershipCommandHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public CommandResult handle(CreateMembershipCommand command) {
        Objects.requireNonNull(command, "Create membership command is required");

        try{
            MemberDiscountValue memberDiscountValue = MemberDiscountValue.builder()
                .unit(command.getDiscountUnit())
                .value(command.getDiscountValue()).build();
        
        var validUntil = command.getValidUntil().isPresent() ? command.getValidUntil().get() :
                LocalDateTime.of(LocalDateTime.now().getYear() + 1, 1,1,0,0);
        
        MembershipType membershipType = MembershipType.builder()
                .id(MembershipTypeId.create().getValue())
                .type(command.getName())
                .description(command.getDescription())
                .memberDiscountValue(memberDiscountValue)
                .requiredPoint(command.getRequiredPoints())
                .validUntil(validUntil)
                .active(true)
                .build();
        
        var savedMembershipType = membershipTypeRepository.save(membershipType);
        
        return CommandResult.success(savedMembershipType.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_membership_type_type")) {
                throw new DomainException("Tên loại thành viên đã tồn tại");
            }
            if (e.getMessage().contains("uk_membership_type_required_point")) {
                throw new DomainException("Điểm thưởng đã tồn tại");
            }
            throw new DomainException("Lỗi khi tạo loại thành viên", e);
        }
    }

    
}
