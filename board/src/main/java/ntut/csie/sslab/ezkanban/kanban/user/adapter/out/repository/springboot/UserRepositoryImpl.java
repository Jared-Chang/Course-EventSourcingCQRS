package ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot;

import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserMapper;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class UserRepositoryImpl implements UserRepository {
    private final UserInBoardRepositoryPeer peer;

    public UserRepositoryImpl(UserInBoardRepositoryPeer peer) {
        requireNotNull("UserInBoardRepositoryPeer", peer);

        this.peer = peer;
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        requireNotNull("User id", userId);

        return peer.findById(userId).map(UserMapper::toDto);
    }

    @Override
    public void save(UserDto user) {
        requireNotNull("UserDto", user);

        peer.save(UserMapper.toData(user));
    }

    @Override
    public void delete(UserDto user) {
        requireNotNull("UserDto", user);
        peer.deleteById(user.getUserId());
    }

    @Override
    public List<UserDto> getUsers(List<String> userIds) {
        requireNotNull("User id list", userIds);

        return UserMapper.toDto(peer.getUsers(userIds));
    }
}
