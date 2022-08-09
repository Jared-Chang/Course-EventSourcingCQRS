package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.boardcontent;


import ntut.csie.sslab.ddd.entity.DomainEvent;
public interface NotifyBoardContent {
    void project(DomainEvent domainEvent);
}

