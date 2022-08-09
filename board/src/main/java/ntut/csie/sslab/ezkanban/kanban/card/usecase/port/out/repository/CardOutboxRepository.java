package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.usecase.GenericOutboxRepository;
import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxStore;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CardOutboxRepository implements CardRepository {
    private final OutboxStore<CardData, String> store;
    private final GenericOutboxRepository<Card, CardData, CardId> outboxRepository;

    public CardOutboxRepository(OutboxStore<CardData, String> store) {
        outboxRepository = new GenericOutboxRepository<>(store, CardMapper.newMapper());
        this.store = store;
    }

    @Override
    public List<Card> findCardsInLane(LaneId laneId) {
        List<CardEvents.CardCreated> cardCreateds = store.getCategoryEvent(CardEvents.TypeMapper.CARD_CREATED)
                .stream().map(x -> (CardEvents.CardCreated) DomainEventMapper.toDomain(x))
                .filter(y -> y.laneId().equals(laneId)).toList();
        List<CardEvents.CardMoved> cardMoveds = store.getCategoryEvent(CardEvents.TypeMapper.CARD_MOVED)
                .stream().map(x -> (CardEvents.CardMoved) DomainEventMapper.toDomain(x))
                .filter(y-> y.newLaneId().equals(laneId) || y.oldLaneId().equals(laneId)).toList();

        List<DomainEvent> events = new ArrayList<>();
        events.addAll(cardCreateds);
        events.addAll(cardMoveds);
        events.sort(Comparator.comparing(DomainEvent::occurredOn));

        List<CardId> cardIdInLane = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            switch (events.get(i)) {
                case CardEvents.CardCreated event -> {
                    cardIdInLane.add(event.cardId());
                }
                case CardEvents.CardMoved event -> {
                    if (event.newLaneId().equals(laneId)) {
                        if (event.oldLaneId().equals(laneId)) {
                            cardIdInLane.remove(event.cardId());
                        }
                        cardIdInLane.add(event.order(), event.cardId());
                    }
                    else if (event.oldLaneId().equals(laneId)) {
                        cardIdInLane.remove(event.cardId());
                    }
                }
                default -> {}
            }
        }

        List<Card> cards = new ArrayList<>();

        cardIdInLane.forEach(cardId -> {
            Optional<Card> card = findById(cardId);
            if (card.isPresent()) {
                cards.add(card.get());
            }
        });
        return cards;
    }

    @Override
    public Optional<Card> findById(CardId cardId) {
        return outboxRepository.findById(cardId);
    }

    @Override
    public void save(Card card) {
        outboxRepository.save(card);
    }

    @Override
    public void delete(Card card) {
        outboxRepository.delete(card);
    }
}
