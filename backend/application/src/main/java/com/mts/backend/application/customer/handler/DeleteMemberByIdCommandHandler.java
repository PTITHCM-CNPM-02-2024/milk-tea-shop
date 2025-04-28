package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.DeleteMemberByIdCommand;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMemberByIdCommandHandler implements ICommandHandler<DeleteMemberByIdCommand, CommandResult> {

    private final JpaMembershipTypeRepository jpaMembershipTypeRepository;

    public DeleteMemberByIdCommandHandler(JpaMembershipTypeRepository jpaMembershipTypeRepository) {
        this.jpaMembershipTypeRepository = jpaMembershipTypeRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeleteMemberByIdCommand command) {
        var membershipType = jpaMembershipTypeRepository.findById(command.getMembershipTypeId().getValue())
                .orElseThrow(() -> new NotFoundException("Membership type not found"));

        var defaultMem = jpaMembershipTypeRepository.findWithMinimumRequiredPoint().orElseThrow(() -> new DomainException("Không tìm thấy " +
                                                                                                                          "một " +
                                                                                                                          "thành viên hợp lệ " +
                                                                                                                          "cho quá trình thiết " +
                                                                                                                          "lập thành viên cho " +
                                                                                                                          "khách hàng"));

        membershipType.getCustomers().forEach(c -> {
            c.setMembershipType(defaultMem);
            c.setCurrentPoint(RewardPoint.of(defaultMem.getRequiredPoint()));
        });

        jpaMembershipTypeRepository.delete(membershipType);

        return CommandResult.success(membershipType.getId());
    }
}