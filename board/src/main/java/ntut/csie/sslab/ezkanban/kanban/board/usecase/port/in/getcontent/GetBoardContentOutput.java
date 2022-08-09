package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;

public class GetBoardContentOutput extends CqrsOutput<GetBoardContentOutput> {

    private BoardContentViewModel viewModel;

    public BoardContentViewModel getViewModel() {
        return viewModel;
    }

    public GetBoardContentOutput setViewModel(BoardContentViewModel viewModel) {
        this.viewModel = viewModel;
        return this;
    }
}
