package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.*;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.stage.CreateStageInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.create.stage.CreateStageUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;

import java.util.UUID;

public class CreateStageService implements CreateStageUseCase {
    private WorkflowRepository workflowRepository;
    private DomainEventBus domainEventBus;

    public CreateStageService(WorkflowRepository workflowRepository,
                              DomainEventBus domainEventBus) {

        this.workflowRepository = workflowRepository;
        this.domainEventBus = domainEventBus;
    }


    @Override
    public CqrsOutput execute(CreateStageInput input) {

        Workflow workflow = workflowRepository.findById(WorkflowId.valueOf(input.getWorkflowId())).orElse(null);
        CqrsOutput output = CqrsOutput.create();

        if (null == workflow){
            output.setId(input.getWorkflowId())
                    .setExitCode(ExitCode.FAILURE)
                    .setMessage("Create stage failed: workflow not found, workflow id = " + input.getWorkflowId());
            domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
            return output;
        }

        workflow.setVersion(input.getVersion());
        LaneId stageId = LaneId.create();
        workflow.createStage(
                LaneId.valueOf(input.getParentId()),
                stageId,
                input.getName(),
                WipLimit.valueOf(input.getWipLimit()),
                LaneType.valueOf(input.getLaneType()),
                input.getUserId());

        workflowRepository.save(workflow);
        domainEventBus.postAll(workflow);

        return output.setId(stageId.id()).setExitCode(ExitCode.SUCCESS);
    }
}
