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
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete.DeleteCardUseCase;
import ntut.csie.sslab.ezkanban.kanban.common.usecase.ClientBoardContentMightExpire;

import java.util.UUID;

public class DeleteCardService implements DeleteCardUseCase {
	private CardRepository cardRepository;
	private DomainEventBus domainEventBus;

	public DeleteCardService(CardRepository cardRepository,
							 DomainEventBus domainEventBus) {

		this.cardRepository = cardRepository;
		this.domainEventBus = domainEventBus;
	}

	@Override
	public CqrsOutput execute(DeleteCardInput input) {

		CqrsOutput output = CqrsOutput.create();

		try {
			Card card = cardRepository.findById(CardId.valueOf(input.getCardId())).orElse(null);
			if (null == card){
				output.setId(input.getCardId())
						.setExitCode(ExitCode.FAILURE)
						.setMessage("Delete card failed: card not found, card id = " + input.getCardId());
				domainEventBus.post(new ClientBoardContentMightExpire(BoardId.valueOf(input.getBoardId()), UUID.randomUUID(), DateProvider.now()));
				return output;
			}

			card.markAsDeleted(input.getUserId());
			cardRepository.delete(card);

			return output.setId(card.getCardId().id()).setExitCode(ExitCode.SUCCESS);
		}
		catch (Exception e){
			throw new UseCaseFailureException(e);
		}
	}
}
