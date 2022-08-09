package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.AbstractRepository;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.tag.entity.Tag;

import java.util.List;

public interface TagRepository extends AbstractRepository<Tag, String> {

    List<Tag> getTagsByBoardId(BoardId boardId);


}
