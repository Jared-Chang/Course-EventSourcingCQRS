package ntut.csie.sslab.ddd.adapter.eventbroker;

public interface EsdbListener extends Runnable {
    void run();

    void close();

    void shutdown();
}
