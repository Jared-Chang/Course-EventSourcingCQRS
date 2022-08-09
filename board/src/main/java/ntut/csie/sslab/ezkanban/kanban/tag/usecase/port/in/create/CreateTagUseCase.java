package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create;

import ntut.csie.sslab.ddd.usecase.cqrs.Command;
import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;

public interface CreateTagUseCase extends Command<CreateTagInput, CqrsOutput> {
}
