package ntut.csie.sslab.ezkanban.kanban.card.entity;

import ntut.csie.sslab.ddd.entity.ValueObject;

import java.util.UUID;

public record CardId(String id) implements ValueObject {

    public static CardId valueOf(String id){
        return new CardId(id);
    }

    public static CardId valueOf(UUID id){
        return new CardId(id.toString());
    }

    public static CardId create(){
        return new CardId(UUID.randomUUID().toString());
    }

    public String toString(){
        return id;
    }
}
