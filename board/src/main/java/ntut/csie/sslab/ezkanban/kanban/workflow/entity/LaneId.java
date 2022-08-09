package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ntut.csie.sslab.ddd.entity.ValueObject;

import java.util.UUID;

public record LaneId(String id) implements ValueObject {
    public final static LaneId NULL = LaneId.valueOf("-1");

    @JsonIgnore
    public boolean isNull() {
       return this.equals(NULL);
    }

    public static LaneId valueOf(String id){
        return new LaneId(id);
    }

    public static LaneId valueOf(UUID id){
        return new LaneId(id.toString());
    }

    public static LaneId create(){
        return new LaneId(UUID.randomUUID().toString());
    }

    public String toString(){
        return id;
    }

}
