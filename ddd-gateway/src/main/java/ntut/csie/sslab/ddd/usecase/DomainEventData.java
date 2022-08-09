package ntut.csie.sslab.ddd.usecase;


import org.json.JSONObject;

import java.util.UUID;

public record DomainEventData(UUID id, String eventType, String contentType, byte[] eventData, byte[] userMetadata) {

    @Override
    public boolean equals(Object that) {
        if(that instanceof DomainEventData target) {
            var thisData = new JSONObject(new String(this.eventData));
            var targetData = new JSONObject(new String(target.eventData));
            var thisMetaData = new JSONObject(new String(this.userMetadata));
            var targetMetaData = new JSONObject(new String(target.userMetadata));

            return  this.id.equals(target.id())&&
                    this.eventType.equals(target.eventType()) &&
                    this.contentType.equals(target.contentType()) &&
                    thisData.similar(targetData) &&
                    thisMetaData.similar(targetMetaData);
        }
        return false;
    }
}
