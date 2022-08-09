package ntut.csie.sslab.ddd.usecase;

import java.io.Closeable;
import java.util.Optional;

public interface AbstractRepository<T, ID> extends Closeable {
    Optional<T> findById(ID id);
    void save(T data);
    void delete(T data);
    default void close(){
        //doing nothing
    }
}
