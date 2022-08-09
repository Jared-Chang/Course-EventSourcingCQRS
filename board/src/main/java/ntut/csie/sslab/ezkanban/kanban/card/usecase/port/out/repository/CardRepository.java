package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository;


import ntut.csie.sslab.ddd.usecase.AbstractRepository;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;

import java.util.List;

public interface CardRepository extends AbstractRepository<Card, CardId> {
	List<Card> findCardsInLane(LaneId laneId);
}

