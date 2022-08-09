package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;


import java.util.List;

public class BoardDto {
	private String boardId;
	private String projectId;
	private String name;
	private List<BoardMemberDto> boardMembers;
	private List<CommittedWorkflowDto> committedWorkflows;
	private int order;
	private long version;

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BoardMemberDto> getBoardMembers() {
		return boardMembers;
	}

	public void setBoardMembers(List<BoardMemberDto> boardMembers) {
		this.boardMembers = boardMembers;
	}

	public List<CommittedWorkflowDto> getCommittedWorkflows() {
		return committedWorkflows;
	}

	public void setCommittedWorkflows(List<CommittedWorkflowDto> committedWorkflows) {
		this.committedWorkflows = committedWorkflows;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
