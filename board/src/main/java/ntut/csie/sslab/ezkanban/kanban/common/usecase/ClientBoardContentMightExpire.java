package ntut.csie.sslab.ezkanban.kanban.common.usecase;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import java.util.Date;
import java.util.UUID;

public record ClientBoardContentMightExpire(BoardId boardId, UUID id, Date occurredOn) implements DomainEvent {

    @Override
    public String aggregateId() {
        return boardId.id();
    }
}
