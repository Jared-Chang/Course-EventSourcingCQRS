package ntut.csie.sslab.ezkanban.kanban.card.usecase.port.in.description;

import ntut.csie.sslab.ddd.usecase.VersionedInput;

public class ChangeCardDescriptionInput implements VersionedInput {
    private String cardId;
    private String description;
    private String boardId;
    private String userId;
    private long version;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
}
