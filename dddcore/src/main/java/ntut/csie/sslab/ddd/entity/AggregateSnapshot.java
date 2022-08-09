package ntut.csie.sslab.ddd.entity;

public interface AggregateSnapshot<T> {
    T getSnapshot();
    void setSnapshot(T snapshot);
}
