package ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.in.create;


import ntut.csie.sslab.ddd.usecase.Input;

public class CreateTagInput implements Input {
    private String tagId;
    private String boardId;
    private String name;

    private String color;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
