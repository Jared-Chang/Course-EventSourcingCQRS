package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.rename.RenameLaneInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.rename.RenameLaneUseCase;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;

import java.util.UUID;

public class RenameLaneService implements RenameLaneUseCase {

    private WorkflowRepository workflowRepository;
    private DomainEventBus domainEventBus;

    public RenameLaneService(WorkflowRepository workflowRepository, DomainEventBus domainEventBus) {
        this.workflowRepository = workflowRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public CqrsOutput execute(RenameLaneInput input) {

        CqrsOutput output = CqrsOutput.create();

        try {
            Workflow workflow= workflowRepository.findById(WorkflowId.valueOf(input.getWorkflowId())).orElse(null);
            if (null == workflow){
                output.setId(input.getWorkflowId())
                        .setExitCode(ExitCode.FAILURE)
                        .setMessage("Rename lane failed: workflow not found, workflow id = " + input.getWorkflowId());
                domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
                return output;
            }

            workflow.setVersion(input.getVersion());
            workflow.renameLane(LaneId.valueOf(input.getLaneId()), input.getNewName(), input.getUserId());

            workflowRepository.save(workflow);
            domainEventBus.postAll(workflow);

            return output.setId(input.getLaneId()).setExitCode(ExitCode.SUCCESS);

        } catch (Exception e){
            throw new UseCaseFailureException(e);
        }
    }
}
