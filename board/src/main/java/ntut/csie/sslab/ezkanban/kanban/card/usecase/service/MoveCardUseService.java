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
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.move.MoveCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.UUID;

public class MoveCardUseService implements MoveCardUseCase {
    private CardRepository cardRepository;
    private DomainEventBus domainEventBus;

    public MoveCardUseService(CardRepository cardRepository,
                              DomainEventBus domainEventBus) {
        this.cardRepository = cardRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public CqrsOutput execute(MoveCardInput input) {
        try {
            Card card = cardRepository.findById(CardId.valueOf(input.getCardId())).orElse(null);
            CqrsOutput output = CqrsOutput.create();

            if (null == card){
                output.setId(input.getCardId())
                        .setExitCode(ExitCode.FAILURE)
                        .setMessage("Move card failed: card not found, card id = " + input.getCardId());
                domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
                return output;
            }

            card.setVersion(input.getVersion());
            card.move(
                    WorkflowId.valueOf(input.getWorkflowId()),
                    LaneId.valueOf(input.getNewLaneId()),
                    input.getUserId(),
                    input.getOrder()
            );

            cardRepository.save(card);
            domainEventBus.postAll(card);

            return output.setId(input.getCardId()).setExitCode(ExitCode.SUCCESS);
        }
        catch (Exception e){
            throw new UseCaseFailureException(e);
        }
    }
}
