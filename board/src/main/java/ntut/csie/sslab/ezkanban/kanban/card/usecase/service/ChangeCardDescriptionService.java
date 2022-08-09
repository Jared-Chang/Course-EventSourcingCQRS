package ntut.csie.sslab.ezkanban.kanban.card.usecase.service;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description.ChangeCardDescriptionUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;

import java.util.UUID;

public class ChangeCardDescriptionService implements ChangeCardDescriptionUseCase {
    private CardRepository cardRepository;
    private DomainEventBus domainEventBus;

    public ChangeCardDescriptionService(CardRepository cardRepository,
                                        DomainEventBus domainEventBus) {

        this.cardRepository = cardRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public CqrsOutput execute(ChangeCardDescriptionInput input) {

        CqrsOutput output = CqrsOutput.create();

        try {
            Card card = cardRepository.findById(CardId.valueOf(input.getCardId())).orElse(null);
            if (null == card){
                output.setId(input.getCardId())
                        .setExitCode(ExitCode.FAILURE)
                        .setMessage("Change card description failed: card not found, card id = " + input.getCardId());
                domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
                return output;
            }

            card.setVersion(input.getVersion());
            card.changeDescription(input.getDescription(), input.getUserId());

            cardRepository.save(card);
            domainEventBus.postAll(card);

            return output.setId(card.getCardId().id()).setExitCode(ExitCode.SUCCESS);
        }
        catch (Exception e){
            throw new UseCaseFailureException(e);
        }
    }
}
