package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.DomainEventData;
import ntut.csie.sslab.ddd.usecase.OutboxData;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="board")
public class BoardData implements OutboxData {

    @Transient
    private String steamName;

    @Transient
    private List<DomainEventData> domainEventDatas;

    @Id
    @Column(name="id")
    private String boardId;

    @Column(name="team_id", nullable = false)
    private String teamId;

    @Column(name="board_name")
    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="id_fk")
    private Set<BoardMemberData> boardMemberDatas;

    //	@Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private long version;

    public BoardData(){
        boardMemberDatas = new HashSet<>();
        domainEventDatas = new ArrayList<>();
    }

    public BoardData(String teamId, String boardId, String name, long version) {
        this();
        this.boardId = boardId;
        this.teamId = teamId;
        this.name = name;
        this.version = version;
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
    @Transient
    public String getId() {
        return boardId;
    }

    @Override
    @Transient
    public void setId(String id) {
        this.boardId = id;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BoardMemberData> getBoardMemberDatas() {
        return new ArrayList<>(boardMemberDatas);
    }

    public void setBoardMemberDatas(List<BoardMemberData> boardMemberDatas) {
        this.boardMemberDatas = new HashSet<>(boardMemberDatas);
    }

    @Override
    @Transient
    public List<DomainEventData> getDomainEventDatas() {
        return domainEventDatas;
    }

    @Override
    @Transient
    public void setDomainEventDatas(List<DomainEventData> domainEventDatas) {
        this.domainEventDatas = domainEventDatas;
    }

    @Override
    @Transient
    public String getStreamName() {
        return steamName;
    }

    @Override
    @Transient
    public void setStreamName(String streamName) {
        this.steamName = streamName;
    }

    @Override
    @Transient
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.boardId);
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.teamId);
        return hash;
    }

    @Override
    @Transient
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BoardData other = (BoardData) obj;
        if (!Objects.equals(this.teamId, other.teamId)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.boardId, other.boardId);
    }
}
