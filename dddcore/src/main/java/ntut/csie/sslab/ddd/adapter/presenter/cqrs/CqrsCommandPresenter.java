package ntut.csie.sslab.ddd.adapter.presenter.cqrs;

import ntut.csie.sslab.ddd.adapter.presenter.Presenter;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;

public class CqrsCommandPresenter implements Presenter<CqrsOutput, CqrsCommandViewModel> {


    public static CqrsCommandPresenter newInstance(){
        return new CqrsCommandPresenter();
    }

    @Override
    public CqrsCommandViewModel buildViewModel(CqrsOutput outputData) {
        CqrsCommandViewModel viewModel = new CqrsCommandViewModel();
        viewModel.setId(outputData.getId());
        viewModel.setMessage(outputData.getMessage());
        viewModel.setExitCode(outputData.getExitCode());

        return viewModel;
    }

}