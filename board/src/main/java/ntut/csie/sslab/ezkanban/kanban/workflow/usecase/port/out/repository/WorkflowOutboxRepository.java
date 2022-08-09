package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.GenericOutboxRepository;
import ntut.csie.sslab.ddd.usecase.OutboxStore;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.Optional;

public class WorkflowOutboxRepository implements WorkflowRepository{
    private OutboxStore<WorkflowData, String> store;
    private final GenericOutboxRepository<Workflow, WorkflowData, WorkflowId> outboxRepository;

    public WorkflowOutboxRepository(OutboxStore<WorkflowData, String> store) {
        outboxRepository = new GenericOutboxRepository<>(store, WorkflowMapper.newMapper());
        this.store = store;
    }

    @Override
    public Optional<Workflow> findById(WorkflowId workflowId) {
        return outboxRepository.findById(workflowId);
    }

    @Override
    public void save(Workflow workflow) {
        outboxRepository.save(workflow);
    }

    @Override
    public void delete(Workflow workflow) {
        outboxRepository.delete(workflow);
    }
}
