package ntut.csie.sslab.ezkanban.kanban.card.adapter.out.repository.springboot;

import ntut.csie.sslab.ddd.framework.OrmClient;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardData;

// generate bean for PostgresOutboxStoreClient

public interface CardOrmClient extends OrmClient<CardData, String> {

}