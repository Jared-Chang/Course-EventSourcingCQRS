package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.create;

import ntut.csie.sslab.ddd.usecase.cqrs.Command;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;

public interface CreateCardUseCase extends Command<CreateCardInput, CqrsOutput> {

}
