package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane.LaneData;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="workflow")
public class WorkflowData implements OutboxData {

    @Transient
    private String streamName;

    @Transient
    private List<DomainEventData> domainEventDatas;

    @Id
    @Column(name="id")
    private String workflowId;

    @Column(name="board_id", nullable = false)
    private String boardId;

    @Column(name="workflow_name")
    private String name;

    // do not add orphanRemoval = true
//    cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER, orphanRemoval = true)
    @ManyToMany(
            cascade =
            {
                CascadeType.DETACH,
                CascadeType.MERGE,
                CascadeType.REFRESH,
                CascadeType.PERSIST
            },
            fetch = FetchType.EAGER)
    @JoinTable(name = "rootstage_in_workflow",
            joinColumns = {@JoinColumn(name = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lane_id")})
    @OrderBy("order")
    private final Set<LaneData> laneDatas;

    // Add this!
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;


    @Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private long version;

    public WorkflowData() {
        this.laneDatas = new HashSet<>();
        this.domainEventDatas = new ArrayList<>();
    }

    public WorkflowData(String workflowId, String boardId, String name, long version) {
        this();
        this.workflowId = workflowId;
        this.boardId = boardId;
        this.name = name;
        this.version = version;
    }

    public void setWorkflowId(String id) {
        this.workflowId = id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public List<LaneData> getLaneDatas() {
        List<LaneData> result = new ArrayList<>(laneDatas);
        result.sort(Comparator.comparing(LaneData::getOrder));
        return result;
    }

    public void addLaneData(LaneData laneData){
        laneDatas.add(laneData);
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String getId() {
        return workflowId;
    }

    @Override
    public void setId(String id) {
        this.workflowId = id;
    }

    @Override
    public List<DomainEventData> getDomainEventDatas() {
        return domainEventDatas;
    }

    @Override
    public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
        this.domainEventDatas = domainEventDatas;
    }

    @Override
    public String getStreamName() {
        return this.streamName;
    }

    @Override
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public long getVersion() {
        return version;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
