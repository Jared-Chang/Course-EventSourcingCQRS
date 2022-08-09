package ntut.csie.sslab.ezkanban.kanban.board.adapter.out.presenter.getcontent;

import ntut.csie.sslab.ddd.adapter.presenter.Presenter;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentViewModel;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.GetBoardContentOutput;

public class GetBoardContentPresenter implements Presenter<GetBoardContentOutput, BoardContentViewModel> {

    @Override
    public BoardContentViewModel buildViewModel(GetBoardContentOutput boardContentOutput) {
        return boardContentOutput.getViewModel();
    }
}
