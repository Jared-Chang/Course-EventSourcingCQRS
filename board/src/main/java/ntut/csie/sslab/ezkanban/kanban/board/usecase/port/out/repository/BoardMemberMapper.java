package ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository;

import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardMember;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BoardMemberMapper {

	public static BoardMemberData toData(BoardMember boardMember) {
		return new BoardMemberData(
				new BoardMemberDataId(boardMember.getBoardId().id(), boardMember.getUserId()),
				boardMember.getBoardRole().name());
	}


	public static List<BoardMemberData> toData(List<BoardMember> boardMembers) {
		List<BoardMemberData> datas = new ArrayList<>();

		boardMembers.forEach(x -> datas.add(toData(x)));

		return datas;
	}

	public static BoardMemberDto toDto(BoardMember boardMember, Map<String, UserDto> userDtoMap) {
		BoardMemberDto dto = new BoardMemberDto();
		dto.setBoardId(boardMember.getBoardId().id());
		dto.setUserId(boardMember.getUserId());
		dto.setEmail(userDtoMap.get(boardMember.getUserId()).getEmail());
		dto.setNickname(userDtoMap.get(boardMember.getUserId()).getNickname());
		dto.setMemberType(boardMember.getBoardRole());
		return dto;
	}

	public static List<BoardMemberDto> toDto(List<BoardMember> boardMembers, Map<String, UserDto> userDtoMap) {
		List<BoardMemberDto> boardMemberDtos = new ArrayList<>();
		for(BoardMember boardMember : boardMembers) {
			boardMemberDtos.add(toDto(boardMember, userDtoMap));
		}

		return boardMemberDtos;
	}

	public static BoardMemberDto toDto(BoardMemberData boardMemberData, Map<String, UserDto> userDtoMap) {
		BoardMemberDto dto = new BoardMemberDto();
		dto.setBoardId(boardMemberData.getBoardId());
		dto.setUserId(boardMemberData.getUserId());
		dto.setEmail(userDtoMap.get(boardMemberData.getUserId()).getEmail());
		dto.setNickname(userDtoMap.get(boardMemberData.getUserId()).getNickname());
		dto.setMemberType(BoardRole.valueOf(boardMemberData.getRole()));
		return dto;
	}

	public static List<BoardMember> toDomain(List<BoardMemberDto> boardMemberDtos) {
		List<BoardMember> boardMembers = new ArrayList<>();
		for(BoardMemberDto boardMemberDto : boardMemberDtos) {
			boardMembers.add(toDomain(boardMemberDto));
		}
		return boardMembers;
	}

	public static BoardMember toDomain(BoardMemberDto boardMemberDto) {
		return new BoardMember(BoardRole.valueOf(boardMemberDto.getMemberType()), BoardId.valueOf(boardMemberDto.getBoardId()), boardMemberDto.getUserId());
	}
}