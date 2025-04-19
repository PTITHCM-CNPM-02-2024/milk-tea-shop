package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateMemberCommandHandler implements ICommandHandler<UpdateMemberCommand, CommandResult> {
    private final JpaMembershipTypeRepository membershipTypeRepository;

    public UpdateMemberCommandHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(UpdateMemberCommand command) {
        Objects.requireNonNull(command, "Update member command is required");

        try {
            MembershipType membershipType = mustExistMembershipType(command.getMemberId());

            membershipType.setType(command.getName());
            MemberDiscountValue memberDiscountValue = MemberDiscountValue.builder()
                    .unit(command.getDiscountUnit())
                    .value(command.getDiscountValue()).build();

            membershipType.setMemberDiscountValue(memberDiscountValue);

            membershipType.setRequiredPoint(command.getRequiredPoints());

            membershipType.setDescription(command.getDescription());

            membershipType.setValidUntil(command.getValidUntil());

            membershipType.setActive(command.isActive());

            return CommandResult.success(membershipType.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_membership_type_type")) {
                throw new DomainException("Tên loại thành viên đã tồn tại");
            }
            if (e.getMessage().contains("uk_membership_type_required_point")) {
                throw new DomainException("Điểm thưởng đã tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật loại thành viên", e);
        }

    }

    private MembershipType mustExistMembershipType(MembershipTypeId membershipTypeId) {
        return membershipTypeRepository.findById(membershipTypeId.getValue())
                .orElseThrow(() -> new NotFoundException("Loại thành viên không tồn tại"));
    }
}
