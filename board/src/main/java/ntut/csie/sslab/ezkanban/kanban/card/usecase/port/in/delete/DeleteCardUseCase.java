package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.delete;

import ntut.csie.sslab.ddd.usecase.cqrs.Command;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;

public interface DeleteCardUseCase extends Command<DeleteCardInput, CqrsOutput> {

}
