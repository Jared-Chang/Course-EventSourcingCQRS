package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="board_content")
public class BoardContentViewData {

	@Id
	@Column(name="board_id")
	private String boardId;

	@Column(name = "view_model", columnDefinition="TEXT")
	private String viewModel;

  	public BoardContentViewData(){}

	public BoardContentViewData(String boardId, String viewModel) {
		this.boardId = boardId;
		this.viewModel = viewModel;
	}

	public String getBoardId() {
		return boardId;
	}

	public String getViewModel() {
		return viewModel;
	}
}
