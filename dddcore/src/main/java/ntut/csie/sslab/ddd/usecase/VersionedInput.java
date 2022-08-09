package ntut.csie.sslab.ddd.usecase;

public interface VersionedInput extends Input {
    long getVersion();

    void setVersion(long version);
}
