package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardRole;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;

public class BoardMemberDto {
    private String boardId;
    @JsonIgnore
    private UserDto userInfo;
    private BoardRole memberType;

    public BoardMemberDto(String boardId, UserDto userInfo, BoardRole memberType) {
        this.boardId = boardId;
        this.userInfo = userInfo;
        this.memberType = memberType;
    }

    public BoardMemberDto() {
        userInfo = new UserDto();
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getUserId() {
        return userInfo.getUserId();
    }

    public void setUserId(String userId) {
        userInfo.setUserId(userId);
    }

    public String getEmail() {
        return userInfo.getEmail();
    }

    public void setEmail(String email) {
        userInfo.setEmail(email);
    }

    public String getNickname() {
        return userInfo.getNickname();
    }

    public void setNickname(String nickname) {
        userInfo.setNickname(nickname);
    }

    public String getMemberType() {
        return memberType.name();
    }

    public void setMemberType(BoardRole memberType) {
        this.memberType = memberType;
    }

}
