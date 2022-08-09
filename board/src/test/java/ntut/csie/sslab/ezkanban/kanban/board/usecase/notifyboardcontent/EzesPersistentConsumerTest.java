package ntut.csie.sslab.ezkanban.kanban.board.usecase.notifyboardcontent;

import ntut.csie.sslab.ddd.framework.ezes.PgMessageDbClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class EzesPersistentConsumerTest extends AbstractBoardContentProjectorTest {


    @Autowired
    PgMessageDbClient pgMessageDbClient;


    @Test
    public void start_consumer() throws SQLException {

//        System.out.println("getStreamVersion : " + pgMessageDbClient.getStreamVersion("Tag-a1367310-47c8-4890-aa2b-a8c7748b6310"));

//        var version = pgMessageDbClient.getStreamVersion("Tag-a1367310-47c8-4890-aa2b-a8c7748b6319");
//        System.out.println("version: " + version.get());

//        var message = pgMessageDbClient.getLastStreamMessage("Tag-a1367310-47c8-4890-aa2b-a8c7748b6319");
//        System.out.println("body: " + message.get().getEventBody());


//        pgMessageDbClient.ack("$all-checkpoint-ezKanban-2", Checkpoint.valueOf(20));
//        pgMessageDbClient.ack("$Tag-a1367310-47c8-4890-aa2b-a8c7748b6319", Checkpoint.valueOf(1));
//        pgMessageDbClient.ack("$$Checkpoint-ezKanban-11", Checkpoint.valueOf(5));

//        PersistentConsumer persistentConsumer = pgMessageDbClient.subscribeToAll("ezKanban-20", 5000);
//        persistentConsumer.run();
    }
}
