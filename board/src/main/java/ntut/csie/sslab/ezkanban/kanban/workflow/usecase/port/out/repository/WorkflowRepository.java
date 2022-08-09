package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.AbstractRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.Workflow;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

public interface WorkflowRepository extends AbstractRepository<Workflow, WorkflowId> {
}
