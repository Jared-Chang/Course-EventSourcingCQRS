package ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent;


import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentViewData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BoardContentReadModelRepositoryPeer extends CrudRepository<BoardContentViewData, String> {

}