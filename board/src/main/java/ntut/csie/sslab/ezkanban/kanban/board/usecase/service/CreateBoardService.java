package ntut.csie.sslab.ezkanban.kanban.board.usecase.service;

import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;
import ntut.csie.sslab.ezkanban.kanban.board.entity.Board;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardBuilder;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardInput;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.create.CreateBoardUseCase;

public class CreateBoardService implements CreateBoardUseCase {
	private final BoardRepository boardRepository;
	private final DomainEventBus domainEventBus;

	public CreateBoardService(BoardRepository boardRepository,
							  DomainEventBus domainEventBus) {

		this.boardRepository = boardRepository;
		this.domainEventBus = domainEventBus;
	}

	@Override
	public CqrsOutput execute(CreateBoardInput input) {

		CqrsOutput output = CqrsOutput.create();

		try {
			Board board = BoardBuilder.newInstance()
					.name(input.getName())
					.teamId(input.getTeamId())
					.boardId(input.getBoardId())
					.build();

			boardRepository.save(board);
			domainEventBus.postAll(board);
		} catch (Exception e) {
			throw new UseCaseFailureException(e);
		}

		return output.setId(input.getBoardId()).setExitCode(ExitCode.SUCCESS);
	}
}
