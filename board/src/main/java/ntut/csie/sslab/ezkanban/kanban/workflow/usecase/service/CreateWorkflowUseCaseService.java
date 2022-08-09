package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.service;

import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowBuilder;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowInput;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.create.CreateWorkflowUseCase;

public class CreateWorkflowUseCaseService implements CreateWorkflowUseCase {
    private final WorkflowRepository workflowRepository;

    public CreateWorkflowUseCaseService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Override
    public CqrsOutput execute(CreateWorkflowInput input) {

        CqrsOutput output = CqrsOutput.create();
        try {
            Workflow workflow = WorkflowBuilder.newInstance()
                    .boardId(input.getBoardId())
                    .name(input.getName())
                    .userId(input.getUserId())
                    .build();

            workflowRepository.save(workflow);
            return output.setId(workflow.getWorkflowId().id()).setExitCode(ExitCode.SUCCESS);
        }
        catch (Exception e) {
            throw new UseCaseFailureException(e);
        }
    }
}
