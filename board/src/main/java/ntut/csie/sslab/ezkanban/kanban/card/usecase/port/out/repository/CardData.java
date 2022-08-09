package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository;


import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name="card")
public class CardData implements OutboxData {

	@Transient
	private List<DomainEventData> domainEventDatas;

	@Transient
	private String streamName;

	@Id
	@Column(name="id")
	private String cardId;

	@Column(name="board_id", nullable = false)
	private String boardId;

	@Column(name="workflow_id", nullable = false)
	private String workflowId;

	@Column(name="lane_id", nullable = false)
	private String laneId;

	@Column(name="description")
	private String description;

	@Column(name="user_id")
	private String userId;

	@Column(name="estimate")
	private String estimate;

	@Column(name="notes", columnDefinition="TEXT")
	private String notes;

	@Column(name="deadline")
	private Instant deadline;

	@Column(name="type")
	private String type;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "tag_in_card", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "tag_id")
	private Set<String> tagIds;

	@Column(name = "last_updated", nullable = false)
	private Date lastUpdated;

	@Version
	@Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
	private long version;

	public CardData(){
		this(0l);
	}

	public CardData(long version){
		tagIds = new HashSet<>();
		this.version = version;
		this.domainEventDatas = new ArrayList<>();
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String workItemId) {
		this.cardId = workItemId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getLaneId() {
		return laneId;
	}

	public void setLaneId(String laneId) {
		this.laneId = laneId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEstimate() {
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getDeadline() {
		return deadline;
	}

	public void setDeadline(Instant deadline) {
		this.deadline = deadline;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addTagId(String tagId) {
		tagIds.add(tagId);
	}

	public Set<String> getTagIds() {
		return tagIds;
	}


	public long getVersion() {
		return version;
	}

	@Override
	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	@Transient
	public String getId() {
		return cardId;
	}

	@Override
	@Transient
	public void setId(String id) {
		this.cardId = id;
	}

	@Override
	@Transient
	public List<DomainEventData> getDomainEventDatas() {
		return this.domainEventDatas;
	}

	@Override
	@Transient
	public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
		this.domainEventDatas = domainEventDatas;
	}

	@Override
	@Transient
	public String getStreamName() {
		return streamName;
	}

	@Override
	@Transient
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
