package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ddd.usecase.DomainEventMapper;
import ntut.csie.sslab.ddd.usecase.OutboxMapper;
import ntut.csie.sslab.ezkanban.kanban.board.entity.*;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardMapper {
    public static BoardData toData(Board board) {
        BoardData data = new BoardData(
                board.getTeamId(),
                board.getId().id(),
                board.getName(),
                board.getVersion());

        data.setId(board.getBoardId().id());
        data.setStreamName(board.getStreamName());
        data.setDomainEventDatas(board.getDomainEvents().stream().map(DomainEventMapper::toData).collect(Collectors.toList()));
        data.setBoardMemberDatas(BoardMemberMapper.toData(board.getMembers()));

        return data;
    }

    public static BoardDto toDto(Board board, Map<String, UserDto> userDtoMap) {
        BoardDto dto = new BoardDto();
        dto.setBoardId(board.getId().id());
        dto.setProjectId(board.getTeamId());
        dto.setName(board.getName());
        dto.setVersion(board.getVersion());
        List<BoardMemberDto> boardMemberDtos = new ArrayList<>();
        for(BoardMember boardMember: board.getMembers()){
            boardMemberDtos.add(BoardMemberMapper.toDto(boardMember, userDtoMap));
        }

        dto.setBoardMembers(boardMemberDtos);
        return dto;
    }

    public static List<BoardDto> toDto(List<Board> boards, Map<String, UserDto> userDtoMap) {
        List<BoardDto> allBoardDtos = new ArrayList<>();
        for(Board board: boards) {
            allBoardDtos.add(toDto(board, userDtoMap));
        }
        return allBoardDtos;
    }

    public static List<Board> toDomain(List<BoardData> datas) {
        List<Board> boards = new ArrayList<>();
        datas.forEach(x -> boards.add(toDomain(x)));
        return boards;
    }

    public static Board toDomain(BoardData data) {

        Board board = new Board(data.getTeamId(), BoardId.valueOf(data.getBoardId()), data.getName());

        for(BoardMemberData boardMemberData : data.getBoardMemberDatas()) {

            BoardRole role = BoardRole.valueOf(boardMemberData.getRole());
            BoardRole boardRole = switch (role) {
                case Admin -> BoardRole.Admin;
                case Member -> BoardRole.Member;
                default -> BoardRole.Guest;
            };

            board.joinAs(boardRole, boardMemberData.getUserId());
        }

        board.setVersion(data.getVersion());
        board.clearDomainEvents();
        return board;
    }

    public static BoardDto toDto(BoardData boardData, Map<String, UserDto> userDtoMap) {
        BoardDto dto = new BoardDto();
        dto.setBoardId(boardData.getBoardId());
        dto.setProjectId(boardData.getTeamId());
        dto.setName(boardData.getName());
        dto.setVersion(boardData.getVersion());
        List<BoardMemberDto> boardMemberDtos = new ArrayList<>();
        for(BoardMemberData boardMember: boardData.getBoardMemberDatas()){
            boardMemberDtos.add(BoardMemberMapper.toDto(boardMember, userDtoMap));
        }

        dto.setBoardMembers(boardMemberDtos);
        return dto;
    }

    private static OutboxMapper mapper = new Mapper();
    public static OutboxMapper newMapper(){
        return mapper;
    }
    static class Mapper implements OutboxMapper<Board, BoardData>{

        @Override
        public Board toDomain(BoardData data) {
            return BoardMapper.toDomain(data);
        }

        @Override
        public BoardData toData(Board aggregateRoot) {
            return BoardMapper.toData(aggregateRoot);
        }
    }
}
