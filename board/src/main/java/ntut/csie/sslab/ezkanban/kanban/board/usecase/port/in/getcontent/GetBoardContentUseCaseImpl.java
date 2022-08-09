package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ddd.usecase.UseCaseFailureException;
import ntut.csie.sslab.ddd.usecase.cqrs.ExitCode;

import java.util.Optional;

public class GetBoardContentUseCaseImpl implements GetBoardContentUseCase {
    private BoardContentReadModelRepository boardContentReadModelRepository;

    public GetBoardContentUseCaseImpl(BoardContentReadModelRepository boardContentReadModelRepository) {
        this.boardContentReadModelRepository = boardContentReadModelRepository;
    }


    @Override
    public GetBoardContentOutput execute(GetBoardContentInput input) {

        GetBoardContentOutput output = new GetBoardContentOutput();
        Optional<BoardContentViewModel> boardContent = boardContentReadModelRepository.getBoardContent(input.getBoardId(), DateProvider.now());

        try {
            if (boardContent.isEmpty()) {
                output.setExitCode(ExitCode.FAILURE)
                        .setMessage("Get board failed: board not found, board id = " + input.getBoardId());
                return output;
            }

        } catch (Exception e) {
            throw new UseCaseFailureException(e);
        }
        return output.setViewModel(boardContent.get()).setExitCode(ExitCode.SUCCESS);
    }
}
