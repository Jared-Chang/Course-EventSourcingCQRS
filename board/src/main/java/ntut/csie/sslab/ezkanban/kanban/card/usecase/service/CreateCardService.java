package ntut.csie.sslab.ezkanban.kanban.card.usecase.service;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.card.entity.Card;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardBuilder;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardRepository;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardInput;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create.CreateCardUseCase;

import java.util.UUID;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class CreateCardService implements CreateCardUseCase {
	private CardRepository cardRepository;
	private DomainEventBus domainEventBus;
	
	public CreateCardService(CardRepository cardRepository, DomainEventBus domainEventBus) {
		requireNotNull("CardRepository", cardRepository);
		requireNotNull("DomainEventBus", domainEventBus);

		this.cardRepository = cardRepository;
		this.domainEventBus = domainEventBus;
	}
	
	
	@Override
	public CqrsOutput execute(CreateCardInput input) {
		
		CqrsOutput output = CqrsOutput.create();
		
		try{
			Card card = CardBuilder.newInstance()
					.cardId(UUID.randomUUID().toString())
					.workflowId(input.getWorkflowId())
					.laneId(input.getLaneId())
					.userId(input.getUserId())
					.description(input.getDescription())
					.boardId(input.getBoardId())
					.expectedOrder(input.getExpectedOrder())
					.build();

			cardRepository.save(card);
			domainEventBus.postAll(card);

			return output.setId(card.getCardId().id()).setExitCode(ExitCode.SUCCESS);
		}
		catch (Exception e){
			throw new UseCaseFailureException(e);
		}
	}
}
