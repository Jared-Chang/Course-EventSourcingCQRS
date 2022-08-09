package ntut.csie.sslab.ddd.adapter.eventbroker;

import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.DomainEventTypeMapper;
import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ddd.usecase.DomainEventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostgresDomainEventListener implements Runnable {

    private final Connection conn;
    private final org.postgresql.PGConnection pgconn;
    private final DomainEventTypeMapper domainEventTypeMapper;
    private final DomainEventBus domainEventBus;
    private boolean keepRunning;

    // MILLISECONDS
    private int poolingInterval = 500;

    public PostgresDomainEventListener(String connectionString,
                                       String user,
                                       String password,
                                       int poolingInterval,
                                       DomainEventTypeMapper domainEventTypeMapper,
                                       DomainEventBus domainEventBus) throws SQLException {
        this.conn = DriverManager.getConnection(connectionString, user, password);
        this.pgconn = (org.postgresql.PGConnection) conn;
        this.poolingInterval = poolingInterval;
        this.domainEventTypeMapper = domainEventTypeMapper;
        this.domainEventBus = domainEventBus;
        keepRunning = true;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("LISTEN domain_event");
        }
    }

    @Override
    public void run() {
        System.out.println("Postgres listener starts");

        while (keepRunning) {
            try {
                // issue a dummy query to contact the backend
                // and receive any pending notifications.
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();

                org.postgresql.PGNotification[] notifications = pgconn.getNotifications();
                if (Objects.nonNull(notifications)) {
                    for (org.postgresql.PGNotification notification : notifications) {
                        JSONObject domainEventJson = new JSONObject(notification.getParameter());
                        String eventType = domainEventJson.getString("type");
                        JSONObject data = domainEventJson.getJSONObject("data");
                        DomainEvent domainEvent = toDomain(eventType, data.toString().getBytes(StandardCharsets.UTF_8));
                        domainEventBus.post(domainEvent);
                    }
                }

                // wait a while before checking again for new notifications
                TimeUnit.MILLISECONDS.sleep(poolingInterval);
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException ie) {
                keepRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void close(){

        try {
            if(null != pgconn)
                pgconn.cancelQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (null != conn)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void shutdown(){
        keepRunning = false;
        close();
        Thread.currentThread().interrupt();
    }

    public DomainEvent toDomain(String eventType, byte[] eventData) {
        try {
            Class<?> cls = domainEventTypeMapper.toClass(eventType);
            Object domainEvent = Json.readAs(
                    eventData, cls);
            return (DomainEvent) domainEvent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
