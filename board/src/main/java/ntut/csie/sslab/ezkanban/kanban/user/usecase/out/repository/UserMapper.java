package ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository;


import java.util.ArrayList;
import java.util.List;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class UserMapper {
	public static List<UserData> toData(List<UserDto> users) {
		List<UserData> results = new ArrayList<>();
		users.forEach(user -> results.add(toData(user)));
		return results;
	}

	public static UserData toData(UserDto user) {
		UserData data = new UserData();
		data.setId(user.getUserId());
		data.setEmail(user.getEmail());
		data.setNickname(user.getNickname());
		return data;
	}

	public static List<UserDto> toDto(List<UserData> users) {
		requireNotNull("User data list", users);

		List<UserDto> results = new ArrayList<>();
		users.forEach(user -> results.add(toDto(user)));
		return results;
	}

	public static UserDto toDto(UserData userData) {
		requireNotNull("UserData", userData);

		UserDto dto = new UserDto();
		dto.setUserId(userData.getId());
		dto.setEmail(userData.getEmail());
		dto.setNickname(userData.getNickname());
		return dto;
	}
}
