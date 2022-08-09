package ntut.csie.sslab.ddd.adapter.eventbroker;

import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import com.eventstore.dbclient.EventStoreDBProjectionManagementClient;
import com.eventstore.dbclient.UpdateProjectionOptions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = {EsdbProjectionTest.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:ddd-gateway-test.properties")
@Disabled
public class EsdbProjectionTest {
    @Value("${esdb.url}")
    private String ESDB_URL;
    private EventStoreDBProjectionManagementClient client;
    private static final String projectionScript = """
                                                    fromStreams(["$et-CardEvents$CardCreated",
                                                                "$et-CardEvents$CardMoved",
                                                                "$et-CardEvents$CardDeleted",
                                                                "$et-WorkflowEvents$WorkflowCreated",
                                                                "$et-WorkflowEvents$StageCreated",
                                                                "$et-WorkflowEvents$SwimLaneCreated",
                                                                "$et-WorkflowEvents$LaneMoved",
                                                                "$et-WorkflowEvents$LaneCopied",
                                                                "$et-WorkflowEvents$LaneDeleted"])
                                                    .when({
                                                        $init: function(){
                                                            return {
                                                            }
                                                        },
                                                        $any: function(s,e) {
                                                            linkTo("CfdEvents-by-Workflow-" +e.body["workflowId"]["id"], e);
                                                        },
                                                    });
                                                    """;

    @BeforeEach
    public void setUp() {
        EventStoreDBClientSettings settings = EventStoreDBConnectionString.parseOrThrow(ESDB_URL);
        client = EventStoreDBProjectionManagementClient.create(settings);
    }

    @Test
    public void create_projection() throws ExecutionException, InterruptedException {
        String projectionName = UUID.randomUUID().toString();
        create(projectionName, client);
    }

    @Test
    public void create_conflict_projection() throws ExecutionException, InterruptedException {
        String projectionName = UUID.randomUUID().toString();
        create(projectionName, client);
        createConflict(projectionName, client);
    }

    @Test
    public void update_projection() throws ExecutionException, InterruptedException {
        String projectName = "projectName";
        createConflict(projectName, client);

        UpdateProjectionOptions options = UpdateProjectionOptions.get().emitEnabled(true);
        client.update(projectName, projectionScript, options).get();
    }

    private static void create(String projectionName, EventStoreDBProjectionManagementClient client)
            throws InterruptedException, ExecutionException {
        // region CreateContinuous

        client.create(projectionName, projectionScript).get();
        // endregion CreateContinuous
    }

    private static void createConflict(String projectionName, EventStoreDBProjectionManagementClient client)
            throws InterruptedException, ExecutionException {
        String js = "{}";

        // region CreateContinuous_Conflict
        try {
            client.create(projectionName, js).get();
        } catch (ExecutionException ex) {
            if (ex.getMessage().contains("Conflict")) {
                System.out.println(projectionName + " already exists");
            }
        }
        // endregion CreateContinuous_Conflict
    }
}
