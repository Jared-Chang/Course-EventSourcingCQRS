package ntut.csie.sslab.ezkanban.kanban.board.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BoardRole {
    @JsonProperty("Admin") Admin,
    @JsonProperty("Member") Member,
    @JsonProperty("Guest") Guest;
}


