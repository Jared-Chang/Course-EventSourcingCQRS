package ntut.csie.sslab.ezkanban.kanban.user.adapter.out.repository.springboot;


import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserInBoardRepositoryPeer extends CrudRepository<UserData, String> {

    @Query(value = "select * from users where users.id in :userIds",
            nativeQuery = true)
    List<UserData> getUsers(@Param("userIds") List<String> userIds);
}