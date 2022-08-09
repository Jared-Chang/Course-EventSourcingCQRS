package ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository;


import ntut.csie.sslab.ddd.usecase.AbstractRepository;

import java.util.List;

public interface UserRepository extends AbstractRepository<UserDto, String> {

    List<UserDto> getUsers(List<String> userIds);
}
