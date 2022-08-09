package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tag")
public class TagData implements OutboxData {
    @Transient
    private String steamName;
    @Transient
    private List<DomainEventData> domainEventDatas;
    @Id
    @Column(name="id")
    private String id;
    @Column(name = "board_id", nullable = false)
    private BoardId boardId;
    @Column(name = "tag_name")
    private String name;
    @Column(name = "tag_color")
    private String color;
    @Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private long version;

    public TagData(){
        this(0l);
    }

    public TagData(long version){
        this.version = version;
    }


    public BoardId getBoardId() {
        return boardId;
    }

    public void setBoardId(BoardId boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public List<DomainEventData> getDomainEventDatas() {
        return domainEventDatas;
    }

    @Override
    public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
        this. domainEventDatas = domainEventDatas;
    }

    @Override
    public String getStreamName() {
        return steamName;
    }

    @Override
    public void setStreamName(String streamName) {
        this.steamName = streamName;
    }

}
