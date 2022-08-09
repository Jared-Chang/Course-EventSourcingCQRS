package ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot;

import ntut.csie.sslab.ezkanban.kanban.common.usecase.AbstractSpringBootJpaTest;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserRepositoryImplTest extends AbstractSpringBootJpaTest {

    @Test
    public void should_succeed_when_get_users() {

        List<String> assigneeIds;
        String assigneeId = "assigneeId";
        String assigneeEmail = "assigneeEmail";
        String assigneeNickname = "assigneeNickname";

        for(int i = 0; i < 10; i++){
            UserDto userDto = new UserDto(assigneeId + i, assigneeEmail + i, assigneeEmail + i);
            userRepository.save(userDto);
        }

        assigneeIds = Arrays.asList("assigneeId0", "assigneeId6");
        List<UserDto> users = userRepository.getUsers(assigneeIds);
        assertEquals(2, users.size());
        assertTrue(users.stream().filter(x -> x.getUserId().equals("assigneeId0")).findFirst().isPresent());
        assertTrue(users.stream().filter(x -> x.getUserId().equals("assigneeId6")).findFirst().isPresent());
    }


}
