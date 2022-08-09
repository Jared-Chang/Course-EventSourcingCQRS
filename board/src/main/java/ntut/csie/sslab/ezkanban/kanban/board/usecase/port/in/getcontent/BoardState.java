package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardMember;
import org.eclipse.persistence.internal.cache.Clearable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonDeserialize(as = BoardState.BoardStateImpl.class)
public interface BoardState extends Clearable {
    void boardId(BoardId boardId);
    void teamId(String teamId);
    void name(String name);
    boolean isDeleted();
    void isDeleted(boolean deleted);
    String name();
    String teamId();
    BoardId boardId();

    List<BoardMember> boardMembers();

    static BoardState create() {
        return new BoardStateImpl();
    }

    class BoardStateImpl implements BoardState {

        private BoardId boardId;
        private String teamId;
        private String name;
        @JsonIgnore
        private boolean isDeleted;
        private final List<BoardMember> boardMembers;
        public BoardStateImpl(){
            super();
            isDeleted = false;
            boardMembers = Collections.synchronizedList(new ArrayList<>());
        }

        @Override
        public void name(String name) {
            this.name = name;
        }

        @JsonIgnore
        @Override
        public boolean isDeleted() {
            return isDeleted;
        }

        @JsonIgnore
        @Override
        public void isDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
        }

        @Override
        public List<BoardMember> boardMembers() {
            return boardMembers;
        }

        @Override
        public BoardId boardId() {
            return boardId;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String teamId() {
            return teamId;
        }

        @Override
        public void boardId(BoardId boardId) {
            this.boardId = boardId;
        }

        @Override
        public void teamId(String teamId){
            this.teamId = teamId;
        }

        @Override
        public void clear(){
            boardMembers.clear();
        }
    }
}
