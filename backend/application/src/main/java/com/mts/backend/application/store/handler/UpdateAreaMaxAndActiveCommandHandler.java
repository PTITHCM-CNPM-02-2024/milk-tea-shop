package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateAreaMaxAndActiveCommand;
import com.mts.backend.application.store.event.ChangeAreaMaxAndActiveEvent;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAreaMaxAndActiveCommandHandler implements ICommandHandler<UpdateAreaMaxAndActiveCommand, CommandResult> {

    private final IAreaRepository areaRepository;

    private final ApplicationEventPublisher eventPublisher;

    public UpdateAreaMaxAndActiveCommandHandler(IAreaRepository areaRepository, IServiceTableRepository serviceTableRepository, ApplicationEventPublisher eventPublisher) {
        this.areaRepository = areaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CommandResult handle(UpdateAreaMaxAndActiveCommand command) {
        Objects.requireNonNull(command, "Update area max table command is required");

        var area = mustExistArea(AreaId.of(command.getAreaId()));

        var maxTable = command.getMaxTable() != null ? MaxTable.of(command.getMaxTable()) : null;
        
        area.changeMaxTable(maxTable);
        area.changeIsActive(command.isActive());
        
        var event = new ChangeAreaMaxAndActiveEvent(this,
                ChangeAreaMaxAndActiveEvent.Data.builder().maxTable(maxTable).isActive(area.isActive()).id(area.getId()).build());

        eventPublisher.publishEvent(event);


        var savedArea = areaRepository.save(area);

        return CommandResult.success(savedArea.getId().getValue());
    }

    private Area mustExistArea(AreaId areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException("Khu vực không tồn tại"));
    }

    // TODO: Implement verifyMaxTable method


}
