package ntut.csie.sslab.ezkanban.kanban.card.entity;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CardIdTest {

    private String userId = "userId";


    @Test
    public void card_id_must_override_to_string_to_generate_event_store_stream_name() {
        Assertions.assertEquals("card id", new CardId("card id").toString());
    }

}
