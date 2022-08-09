package ntut.csie.sslab.ddd.framework.ezes;

import ntut.csie.sslab.ddd.entity.common.Json;

import java.util.concurrent.TimeUnit;

public class PersistentConsumer implements Runnable {

    private final String name;

    private final String streamName;

    // MILLISECONDS
    private int pollingInterval = 500;

    private PgMessageDbClient pgMessageDbClient;
    public PersistentConsumer(String streamName, String consumerName, PgMessageDbClient pgMessageDbClient, int pollingInterval)  {
        this.streamName = streamName;
        this.name = consumerName;
        this.pgMessageDbClient = pgMessageDbClient;
        this.pollingInterval = pollingInterval;
    }

    public void ack(Checkpoint checkpoint){
        pgMessageDbClient.ack(streamName, checkpoint);
    }

    @Override
    public void run() {
        Checkpoint checkpoint;
        var message = pgMessageDbClient.getLastStreamMessage(streamName);
        if (message.isPresent()) {
            checkpoint = Json.readValue(message.get().getEventBody(), Checkpoint.class);
        } else {
            checkpoint = Checkpoint.valueOf(0);
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                var messages = pgMessageDbClient.findAllStream(checkpoint.position());
                if (messages.size() > 0) {
                    for (var each : messages) {
                        // handle event here
                        System.out.println("Read event : " + each.getEventBody().toString());
                    }

                    checkpoint = Checkpoint.valueOf(messages.size());
                    ack(checkpoint);
                }
                TimeUnit.MILLISECONDS.sleep(pollingInterval);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void close(){
    }

    public void shutdown(){
        close();
        Thread.currentThread().interrupt();
    }

}
