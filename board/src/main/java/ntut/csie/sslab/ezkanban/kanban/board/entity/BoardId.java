package ntut.csie.sslab.ezkanban.kanban.board.entity;

import ntut.csie.sslab.ddd.entity.ValueObject;

import java.util.UUID;

public record BoardId(String id) implements ValueObject {

    public static BoardId valueOf(String id){
        return new BoardId(id);
    }

    public static BoardId valueOf(UUID id){
        return new BoardId(id.toString());
    }

    public static BoardId create(){
        return new BoardId(UUID.randomUUID().toString());
    }

    public String toString(){
        return id;
    }

}
