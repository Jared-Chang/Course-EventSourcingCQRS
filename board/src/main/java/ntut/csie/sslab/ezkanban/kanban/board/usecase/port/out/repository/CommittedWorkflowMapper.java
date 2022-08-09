package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ezkanban.kanban.board.entity.CommittedWorkflow;

import java.util.ArrayList;
import java.util.List;

public class CommittedWorkflowMapper {

    public static CommittedWorkflowData toData(CommittedWorkflow committedWorkflow){
        return new CommittedWorkflowData(
                new CommittedWorkflowDataId(committedWorkflow.boardId().id(), committedWorkflow.workflowId().id()),
                committedWorkflow.order()
        );
    }

    public static List<CommittedWorkflowData> toData(List<CommittedWorkflow> committedWorkflows){
        List<CommittedWorkflowData> datas = new ArrayList<>();
        committedWorkflows.forEach( x -> datas.add(toData(x)));
        return datas;
    }

    public static CommittedWorkflowDto toDto(CommittedWorkflow committedWorkflow){
        CommittedWorkflowDto committedWorkflowDto = new CommittedWorkflowDto();
        committedWorkflowDto.setBoardId(committedWorkflow.boardId().id());
        committedWorkflowDto.setWorkflowId(committedWorkflow.workflowId().id());
        committedWorkflowDto.setOrder(committedWorkflow.order());
        return committedWorkflowDto;
    }

    public static List<CommittedWorkflowDto> toDto(List<CommittedWorkflow> committedWorkflows){
        List<CommittedWorkflowDto> committedWorkflowDtos = new ArrayList<>();
        for(CommittedWorkflow committedWorkflow :committedWorkflows){
            committedWorkflowDtos.add(toDto(committedWorkflow));
        }
        return committedWorkflowDtos;
    }

    public static CommittedWorkflowDto toDto(CommittedWorkflowData committedWorkflow){
        CommittedWorkflowDto committedWorkflowDto = new CommittedWorkflowDto();
        committedWorkflowDto.setBoardId(committedWorkflow.getBoardId());
        committedWorkflowDto.setWorkflowId(committedWorkflow.getWorkflowId());
        committedWorkflowDto.setOrder(committedWorkflow.getOrder());
        return committedWorkflowDto;
    }
}
