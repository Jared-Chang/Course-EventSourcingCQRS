package ntut.csie.sslab.ezkanban.kanban.workflow.adapter.out.repository.springboot;

import ntut.csie.sslab.ddd.framework.OrmClient;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowData;

public interface WorkflowOrmClient extends OrmClient<WorkflowData, String> {

}
