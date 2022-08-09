package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.EventStore;
import ntut.csie.sslab.ddd.usecase.GenericEventSourcingRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.List;
import java.util.Optional;

public class WorkflowEventSourcingRepository implements WorkflowRepository {
    private final GenericEventSourcingRepository<Workflow> eventSourcingRepository;

    public WorkflowEventSourcingRepository(EventStore eventStore) {
        eventSourcingRepository = new GenericEventSourcingRepository<>(eventStore, Workflow.class, Workflow.CATEGORY);
    }

    @Override
    public Optional<Workflow> findById(WorkflowId workflowId) {
        return eventSourcingRepository.findById(workflowId.id());
    }

    @Override
    public void save(Workflow workflow) {
        eventSourcingRepository.save(workflow);
    }

    @Override
    public void delete(Workflow workflow) {
        eventSourcingRepository.delete(workflow);
    }

    @Override
    public void close() {
        eventSourcingRepository.close();
    }
}
