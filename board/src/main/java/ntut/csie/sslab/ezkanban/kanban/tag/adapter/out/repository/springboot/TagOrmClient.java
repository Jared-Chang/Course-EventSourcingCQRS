package ntut.csie.sslab.ezkanban.kanban.tag.adapter.out.repository.springboot;

import ntut.csie.sslab.ddd.framework.OrmClient;
import ntut.csie.sslab.ezkanban.kanban.tag.usecase.port.out.repository.TagData;

// generate bean for PostgresOutboxStoreClient

public interface TagOrmClient extends OrmClient<TagData, String> {

}