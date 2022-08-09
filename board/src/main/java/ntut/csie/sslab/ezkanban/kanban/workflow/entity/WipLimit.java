package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import ntut.csie.sslab.ddd.entity.ValueObject;

public record WipLimit(int value) implements ValueObject {

	public static final WipLimit UNLIMIT = new WipLimit(-1);

	public static WipLimit valueOf(int value){
		return new WipLimit(value);
	}

}
