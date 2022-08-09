package ntut.csie.sslab.ezkanban.kanban.board.usecase.getboardcontent;


import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final List<UserDto> store;

    public InMemoryUserRepository(){
        store = new ArrayList<>();
    }

    @Override
    public List<UserDto> getUsers(List<String> userIds) {
        return store;
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        return store.stream().filter(x -> x.getUserId().equals(userId)).findAny();
    }

    @Override
    public void save(UserDto user) {
        store.add(user);
    }

    @Override
    public void delete(UserDto user) {
        store.removeIf( x-> x.getUserId().equals(user.getUserId()));
    }
}
