package ntut.csie.sslab.ddd.entity;

import java.io.Serializable;

public interface Entity<ID> extends Serializable {
    ID getId();
}
