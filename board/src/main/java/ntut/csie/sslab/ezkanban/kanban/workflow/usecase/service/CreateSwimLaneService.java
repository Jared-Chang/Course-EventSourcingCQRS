package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.*;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane.CreateSwimLaneInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.swimlane.CreateSwimLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;

import java.util.UUID;

public class CreateSwimLaneService implements CreateSwimLaneUseCase {
    private WorkflowRepository workflowRepository;
    private DomainEventBus domainEventBus;

    public CreateSwimLaneService(WorkflowRepository workflowRepository,
                                 DomainEventBus domainEventBus) {

        this.workflowRepository = workflowRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public CqrsOutput execute(CreateSwimLaneInput input) {
        Workflow workflow = workflowRepository.findById(WorkflowId.valueOf(input.getWorkflowId())).orElse(null);
        CqrsOutput output = CqrsOutput.create();

        if (null == workflow){
            output.setId(input.getWorkflowId())
                    .setExitCode(ExitCode.FAILURE)
                    .setMessage("Create swimlane failed: workflow not found, workflow id = " + input.getWorkflowId());
            domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
            return output;
        }

        workflow.setVersion(input.getVersion());
        LaneId swimLaneId = LaneId.create();
        workflow.createSwimLane(
                LaneId.valueOf(input.getParentId()),
                swimLaneId,
                input.getName(),
                WipLimit.valueOf(input.getWipLimit()),
                LaneType.valueOf(input.getLaneType()),
                input.getUserId());

        workflowRepository.save(workflow);
        domainEventBus.postAll(workflow);

        return output.setId(swimLaneId.id()).setExitCode(ExitCode.SUCCESS);
    }
}
