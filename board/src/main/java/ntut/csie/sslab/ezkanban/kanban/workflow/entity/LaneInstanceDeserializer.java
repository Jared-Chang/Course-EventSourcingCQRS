package ntut.csie.sslab.ezkanban.kanban.workflow.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class LaneInstanceDeserializer extends JsonDeserializer<Lane> {
    static final String LANE_LAYOUT = "layout";

    @Override
    public Lane deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        Class<? extends Lane> instanceClass;
        if(isStage(root)) {
            instanceClass = Stage.class;
        } else {
            instanceClass = SwimLane.class;
        }
        return mapper.readValue(root.toString(), instanceClass);
    }

    private boolean isStage(ObjectNode node) {
        return node.findValue(LANE_LAYOUT).textValue().equals(LaneLayout.Vertical.name());
    }
}
