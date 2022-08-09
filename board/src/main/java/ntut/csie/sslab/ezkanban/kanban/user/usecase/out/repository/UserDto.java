package ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository;

public class UserDto {
	private String userId;
	private String email;
	private String nickname;

	public UserDto() {
	}

	public UserDto(String userId, String email, String nickname) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
