package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.CardState;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class CardMapper {

	public static CardData toData(Card card) {

		CardData data = new CardData(card.getVersion());

		data.setId(card.getCardId().id());
		data.setBoardId(card.getBoardId().id());
		data.setWorkflowId(card.getWorkflowId().id());
		data.setLaneId(card.getLaneId().id());
		data.setDescription(card.getDescription());
		data.setUserId(card.getUserId());
		data.setLastUpdated(DateProvider.now());
		data.setStreamName(card.getStreamName());
		data.setDomainEventDatas(card.getDomainEvents().stream().map(DomainEventMapper::toData).collect(Collectors.toList()));

		return data;
	}

	public static List<CardData> toData(List<Card> cards) {
		List<CardData> result = new ArrayList<>();
		cards.forEach(x -> result.add(toData(x)));
		return result;
	}

	public static CardDto toDto(Card card) {
		CardDto result = new CardDto();
		result.setCardId(card.getCardId().id());
		result.setBoardId(card.getBoardId().id());
		result.setDescription(card.getDescription());
		result.setLaneId(card.getLaneId().id());
		result.setVersion(card.getVersion());
		result.setUserId(card.getUserId());
		result.setWorkflowId(card.getWorkflowId().id());

		return result;
	}

	public static Card toDomain(CardData cardData) {
		requireNotNull("CardData", cardData);

		Card card = new Card(
				BoardId.valueOf(cardData.getBoardId()),
				WorkflowId.valueOf(cardData.getWorkflowId()),
				LaneId.valueOf(cardData.getLaneId()),
				CardId.valueOf(cardData.getCardId()),
				cardData.getDescription(),
				0,
				cardData.getUserId());
		card.setVersion(cardData.getVersion());
		card.clearDomainEvents();

		return card;
	}

	public static List<Card> toDomain(List<CardData> cardDatas) {
		requireNotNull("Card data list", cardDatas);

		List<Card> result = new ArrayList<>();
		cardDatas.forEach(x -> result.add(toDomain(x)));
		return result;
	}

	public static CardDto toDto(CardData cardData) {
		requireNotNull("CardData", cardData);

		CardDto dto = new CardDto();
		dto.setCardId(cardData.getCardId());
		dto.setBoardId(cardData.getBoardId());
		dto.setWorkflowId(cardData.getWorkflowId());
		dto.setLaneId(cardData.getLaneId());
		dto.setDescription(cardData.getDescription());
		dto.setUserId(cardData.getUserId());
		dto.setVersion(cardData.getVersion());

		return dto;
	}

	public static Map<String, List<CardDto>> toDto(List<CardData> cardDatas) {
		requireNotNull("Card data list", cardDatas);

		Map<String, List<CardDto>> result = new HashMap<>();
		for(CardData card : cardDatas) {
			if(result.containsKey(card.getLaneId())){
				result.get(card.getLaneId()).add(toDto(card));
			}else{
				List<CardDto> cardDtos = new ArrayList<>();
				cardDtos.add(toDto(card));
				result.put(card.getLaneId(), cardDtos);
			}
		}
		return result;
	}


	public static CardDto toDto(CardState card) {
		CardDto result = new CardDto();
		result.setCardId(card.cardId().id());
		result.setBoardId(card.boardId().id());
		result.setDescription(card.description());
		result.setLaneId(card.laneId().id());
		result.setVersion(card.version());

		result.setUserId(card.userId());
		result.setWorkflowId(card.workflowId().id());
		return result;
	}

	private static OutboxMapper mapper = new Mapper();
	public static OutboxMapper newMapper(){
		return mapper;
	}
	static class Mapper implements OutboxMapper<Card, CardData>{

		@Override
		public Card toDomain(CardData data) {
			return CardMapper.toDomain(data);
		}

		@Override
		public CardData toData(Card aggregateRoot) {
			return CardMapper.toData(aggregateRoot);
		}
	}
}
