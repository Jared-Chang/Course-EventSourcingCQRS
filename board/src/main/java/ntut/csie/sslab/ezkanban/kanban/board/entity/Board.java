package ntut.csie.sslab.ezkanban.kanban.board.entity;

import ntut.csie.sslab.ddd.entity.AggregateRoot;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static ntut.csie.sslab.ddd.entity.common.Contract.ensure;
import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class Board extends AggregateRoot<BoardId,DomainEvent> {

	public final static String CATEGORY = "Board";
	private String name;
	private String teamId;
	private List<BoardMember> boardMembers;
	private List<CommittedWorkflow> committedWorkflows;

	public Board(List<? extends DomainEvent> domainEvents){
		super(domainEvents);
	}

	public Board(String teamId, BoardId boardId, String name) {
		super(boardId);

		requireNotNull("Team id", teamId);
		requireNotNull("board id", boardId);
		requireNotNull("Board name", name);

		apply(new BoardEvents.BoardCreated(teamId, boardId, name, UUID.randomUUID(), DateProvider.now()));
	}

	public void rename(String newName) {
		requireNotNull("Board name", newName);

		if((name.equals(newName)))
			return;

		apply(new BoardEvents.BoardRenamed(teamId, getBoardId(), newName, UUID.randomUUID(), DateProvider.now()));

		ensure(format("Board name is '%s'", newName), () -> getName().equals(newName));
	}

	public void commitWorkflow(WorkflowId workflowId) {
		requireNotNull("Workflow id", workflowId);

		apply(new BoardEvents.WorkflowCommitted(getBoardId(), workflowId, UUID.randomUUID(), DateProvider.now()));

		ensure(format("Workflow '%s' is committed", workflowId), () -> getCommittedWorkflow(workflowId).isPresent());
	}

	public Optional<CommittedWorkflow> getCommittedWorkflow(WorkflowId workflowId) {
		requireNotNull("Workflow id", workflowId);

		return committedWorkflows
				.stream()
				.filter(x->x.workflowId().equals(workflowId))
				.findFirst();
	}

	public void uncommitworkflow(WorkflowId workflowId) {
		requireNotNull("Workflow id", workflowId);

		apply(new BoardEvents.WorkflowUncommitted(getBoardId(), workflowId, UUID.randomUUID(), DateProvider.now()));

		ensure(format("Workflow '%s' is uncommitted", workflowId), () -> !getCommittedWorkflow(workflowId).isPresent());
	}

	public void joinAs(BoardRole boardRole, String userId) {
		requireNotNull("User id", userId);

		if (existBoardMemberWithSameRole(userId, boardRole)) {
			return;
		}

		apply(new BoardEvents.BoardMemberAdded(
				userId,
				getBoardId(),
				boardRole,
				UUID.randomUUID(),
				DateProvider.now()));

		ensure(format("User '%s' becomes a board member", userId), () -> getMember(userId).isPresent());
		ensure(format("User role is '%s'", boardRole), () -> getMember(userId).get().getBoardRole().equals(boardRole));
	}

	public void removeMember(String userId) {
		requireNotNull("User id", userId);

		apply(new BoardEvents.BoardMemberRemoved(userId, getBoardId(), UUID.randomUUID(), DateProvider.now()));

		ensure(format("Board member '%s' is removed", userId), () -> !getBoardMember(userId).isPresent());
	}


	@Override
	public void markAsDeleted(String userId) {
		apply(new BoardEvents.BoardDeleted(teamId, getBoardId(), userId, UUID.randomUUID(), DateProvider.now()));
	}


	public String getName() {
		return name;
	}

	public BoardId getBoardId() {
		return getId();
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public Optional<BoardMember> getBoardMember(String userId){
		requireNotNull("User id", userId);

		for(BoardMember each : boardMembers){
			if(each.getUserId().equalsIgnoreCase(userId)){
				return Optional.of(each);
			}
		}
		return Optional.empty();
	}

	public boolean isBoardMember(String userId){
		requireNotNull("User id", userId);

		return getBoardMember(userId).isPresent();
	}

	public List<CommittedWorkflow> getCommittedWorkflows() {
		return committedWorkflows;
	}


	public Optional<BoardMember> getMember(String userId) {
		requireNotNull("User id", userId);

		for(BoardMember each : boardMembers){
			if(each.getUserId().equalsIgnoreCase(userId)){
				return Optional.of(each);
			}
		}
		return Optional.empty();
	}


	private void addCommittedWorkflow(WorkflowId workflowId) {
		requireNotNull("Workflow id", workflowId);

		int order = 0;
		if(committedWorkflows.size() > 0) {
			order = committedWorkflows.get(committedWorkflows.size()-1).order() + 1;
		}
		CommittedWorkflow committedWorkflow = new CommittedWorkflow(getBoardId(), workflowId, order);
		committedWorkflows.add(committedWorkflow);

		ensure(format("Workflow '%s' is committed", workflowId), () -> getCommittedWorkflow(workflowId).isPresent());
	}

	private void removeCommittedWorkflow(WorkflowId workflowId) {
		committedWorkflows.removeIf(x-> x.workflowId().equals(workflowId));
		reorderWorkflow();
	}

	private void reorderWorkflow() {
		List<CommittedWorkflow> newCommittedWorkflows = new ArrayList<>();
		for(int i = 0; i < committedWorkflows.size(); i++){
			CommittedWorkflow each = committedWorkflows.get(i);
			newCommittedWorkflows.add(new CommittedWorkflow(each.boardId(), each.workflowId(), i));
		}

		committedWorkflows.clear();
		committedWorkflows.addAll(newCommittedWorkflows);
	}
	private boolean existBoardMemberWithSameRole(String userId, BoardRole boardRole) {
		return getMember(userId).isPresent() &&
				getMember(userId).get().getBoardRole().equals(boardRole);
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}

	public List<BoardMember> getMembers() {
		return boardMembers;
	}


	@Override
	protected void when(DomainEvent domainEvent) {
		switch (domainEvent) {
			case BoardEvents.BoardCreated event -> {
				this.id = event.boardId();
				this.name = event.boardName();
				this.teamId = event.teamId();
				boardMembers = new ArrayList<>();
				committedWorkflows = new ArrayList<>();
			}
			case BoardEvents.BoardRenamed event -> name = event.boardName();
			case BoardEvents.BoardDeleted event -> {
				committedWorkflows.clear();
				isDeleted = true;
			}
			case BoardEvents.BoardMemberAdded event ->{
				BoardMember boardMember = BoardMemberBuilder.newInstance()
						.memberType(event.boardRole())
						.boardId(event.boardId())
						.userId(event.userId())
						.build();
				boardMembers.add(boardMember);
			}
			case BoardEvents.BoardMemberRemoved event ->  boardMembers.removeIf(x -> x.getUserId().equals(event.userId()));
			case BoardEvents.WorkflowCommitted event -> addCommittedWorkflow(event.workflowId());
			case BoardEvents.WorkflowUncommitted event -> removeCommittedWorkflow(event.workflowId());
			default -> {
			}
		}
	}

}
