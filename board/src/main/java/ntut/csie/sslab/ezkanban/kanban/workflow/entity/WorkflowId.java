package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import ntut.csie.sslab.ddd.entity.ValueObject;

import java.util.UUID;

public record WorkflowId(String id) implements ValueObject {

    public static WorkflowId valueOf(String id){
        return new WorkflowId(id);
    }

    public static WorkflowId valueOf(UUID id){
        return new WorkflowId(id.toString());
    }

    public static WorkflowId create(){
        return new WorkflowId(UUID.randomUUID().toString());
    }

    public String toString(){
        return id;
    }
}
