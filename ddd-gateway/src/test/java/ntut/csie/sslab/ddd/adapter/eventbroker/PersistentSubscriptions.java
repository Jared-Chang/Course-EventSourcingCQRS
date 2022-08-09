package ntut.csie.sslab.ddd.adapter.eventbroker;

import com.eventstore.dbclient.*;

import java.util.concurrent.ExecutionException;

public class PersistentSubscriptions {

    public static final String STREAM_NAME = "$ce-Card";

    public static void createPersistentSubscription(EventStoreDBPersistentSubscriptionsClient client) throws ExecutionException, InterruptedException {
        // region create-persistent-subscription-to-stream
        client.create(
                STREAM_NAME,
                "notify-workflow",
                PersistentSubscriptionSettings.builder()
                        .fromStart()
                        .build()).get();
        // region create-persistent-subscription-to-stream
    }


    public static void connectToPersistentSubscriptionToStream(EventStoreDBPersistentSubscriptionsClient client) {

        client.subscribe(
                STREAM_NAME,
                "notify-workflow",
                new PersistentSubscriptionListener() {
                    @Override
                    public void onEvent(PersistentSubscription subscription, ResolvedEvent event) {
                        System.out.println("Received event"
                                + event.getOriginalEvent().getStreamRevision()
                                + "@" + event.getOriginalEvent().getStreamId()
                                + "---- " + event.getEvent().getEventType());

                        subscription.ack(event);
                    }

                    @Override
                    public void onError(PersistentSubscription subscription, Throwable throwable) {
                        System.out.println("Subscription was dropped due to " + throwable.getMessage());
                    }

                    @Override
                    public void onCancelled(PersistentSubscription subscription) {
                        System.out.println("Subscription is cancelled");
                    }
                });
        // region subscribe-to-persistent-subscription-to-stream
    }


    public static void connectToPersistentSubscriptionToAll(EventStoreDBPersistentSubscriptionsClient client) {

        String excludeSystemEventsRegex = "/^[^\\$].*/";
        SubscriptionFilter filter = SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression(excludeSystemEventsRegex)
                .build();

        PersistentSubscriptionToAllSettings options =
                PersistentSubscriptionToAllSettings.builder()
                .fromStart()
                .build();

        client.subscribeToAll(
                "notify-all",
                new PersistentSubscriptionListener() {
                    @Override
                    public void onEvent(PersistentSubscription subscription, ResolvedEvent event) {
                        System.out.println("Received event from $all stream"
                                + event.getOriginalEvent().getStreamRevision()
                                + "@" + event.getOriginalEvent().getStreamId()
                                + "---- " + event.getEvent().getEventType());

                        subscription.ack(event);
                    }

                    @Override
                    public void onError(PersistentSubscription subscription, Throwable throwable) {
                        System.out.println("Subscription was dropped due to " + throwable.getMessage());
                    }

                    @Override
                    public void onCancelled(PersistentSubscription subscription) {
                        System.out.println("Subscription is cancelled");
                    }
                });
        // region subscribe-to-persistent-subscription-to-stream
    }

}
