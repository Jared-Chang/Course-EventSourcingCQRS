package ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.in.lane;

import ntut.csie.sslab.ezkanban.kanban.workflow.entity.*;

import java.util.ArrayList;
import java.util.List;

public class LaneMapper {

    public static LaneData toData(Lane lane) {
        return toData(lane, null);
    }

    private static LaneData toData(Lane lane, LaneData parent) {
        LaneData laneData = new LaneData(
                lane.getId().id(),
                lane.getWorkflowId().id(),
                parent,
                lane.getName(),
                lane.getWipLimit().value(),
                lane.getOrder(),
                lane.getType().name(),
                lane.getLayout().name()
        );

        for(var _lane : lane.getChildren()) {
            laneData.addChild(LaneMapper.toData(_lane, laneData));
        }

        return laneData;
    }

    public static LaneDto toDto(Lane lane) {
        LaneDto dto= new LaneDto();
        dto.setParentId(lane.getParentId().id());
        dto.setName(lane.getName());
        dto.setLaneId(lane.getId().id());
        dto.setWorkflowId(lane.getWorkflowId().id());
        dto.setType(lane.getType().toString());
        dto.setWipLimit(lane.getWipLimit().value());
        dto.setLayout(lane.getLayout().toString());
        List<LaneDto> laneDtos = new ArrayList<>();

        for(var childLane : lane.getChildren()){
            LaneDto laneDto = toDto(childLane);
            laneDtos.add(laneDto);
        }
        dto.setLanes(laneDtos);
        return dto;
    }

    public static LaneDto toDto(LaneData lane) {
        LaneDto dto= new LaneDto();
        dto.setParentId(lane.getParent() != null ? lane.getParent().getId() : NullLane.ID.id());
        dto.setName(lane.getName());
        dto.setLaneId(lane.getId());
        dto.setWorkflowId(lane.getWorkflowId());
        dto.setType(lane.getType());
        dto.setWipLimit(lane.getWipLimit());
        dto.setLayout(lane.getLayout());
        List<LaneDto> laneDtos = new ArrayList<>();

        for(LaneData childLane: lane.getChildren()){
            LaneDto laneDto = toDto(childLane);
            laneDtos.add(laneDto);
        }
        dto.setLanes(laneDtos);
        return dto;
    }

    public static List<Lane> toDomain(List<LaneDto> laneDtos) {
        return toDomain(laneDtos, NullLane.nullLane);
    }

    public static List<Lane> toDomain(List<LaneDto> laneDtos, Lane parent) {
        List<Lane> lanes = new ArrayList<>();
        for(int i = 0; i < laneDtos.size(); i++) {
            Lane lane = toDomain(laneDtos.get(i), i);
            lanes.add(lane);
        }
        return lanes;
    }

    public static Lane toDomain(LaneDto laneDto, int order) {
        Lane lane;
        if (laneDto.getLayout().equals(LaneLayout.Vertical.name())) {
            lane = new Stage(LaneId.valueOf(laneDto.getLaneId()), WorkflowId.valueOf(laneDto.getWorkflowId()), LaneId.valueOf(laneDto.getParentId()), laneDto.getName(), WipLimit.valueOf(laneDto.getWipLimit()), order, LaneType.valueOf(laneDto.getType()));
        }
        else {
            lane = new SwimLane(LaneId.valueOf(laneDto.getLaneId()), WorkflowId.valueOf(laneDto.getWorkflowId()), LaneId.valueOf(laneDto.getParentId()), laneDto.getName(), WipLimit.valueOf(laneDto.getWipLimit()), order, LaneType.valueOf(laneDto.getType()));
        }
        lane.addChildren(toDomain(laneDto.getLanes(), lane));
        return lane;
    }

}

