package ntut.csie.sslab.ezkanban.kanban.tag.usecase.service;

import ntut.csie.sslab.ddd.usecase.cqrs.CqrsOutput;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagInput;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create.CreateTagUseCase;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagRepository;

public class CreateTagService implements CreateTagUseCase {
    private final TagRepository repository;

    public CreateTagService(TagRepository tagRepository) {
        this.repository = tagRepository;
    }

    public CqrsOutput execute(CreateTagInput input) {

        Tag tag = new Tag(BoardId.valueOf(input.getBoardId()),
                input.getTagId(),
                input.getName(),
                input.getColor());
        repository.save(tag);

        return CqrsOutput.create().setId(tag.getId());
    }

}
