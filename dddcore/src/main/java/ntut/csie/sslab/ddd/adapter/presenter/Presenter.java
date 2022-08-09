package ntut.csie.sslab.ddd.adapter.presenter;

import ntut.csie.sslab.ddd.usecase.Output;

public interface Presenter<D extends Output, M extends ViewModel> {
    M buildViewModel(D data);
}