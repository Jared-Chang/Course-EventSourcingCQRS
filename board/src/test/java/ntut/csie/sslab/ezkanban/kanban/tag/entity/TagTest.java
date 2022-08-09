package ntut.csie.sslab.ezkanban.kanban.tag.entity;

import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {


    @Test
    public void create_a_tag_generates_a_tag_created_event(){
        BoardId boardId = BoardId.create();
        String tagId = UUID.randomUUID().toString();

        Tag tag = new Tag(boardId, tagId, "ntut/csie/sslab/ezkanban/kanban/tag", "Red");

        assertEquals(tagId, tag.getId());
        assertEquals(boardId, tag.getBoardId());
        assertEquals("ntut/csie/sslab/ezkanban/kanban/tag", tag.getName());
        assertEquals("Red", tag.getColor());
        assertFalse(tag.isDeleted());
        assertEquals(1, tag.getDomainEvents().size());
        assertEquals(TagEvents.TagCreated.class, tag.getDomainEvents().get(0).getClass());
    }


    @Test
    public void rename_a_tag(){
        BoardId boardId = BoardId.create();
        String tagId = UUID.randomUUID().toString();
        Tag tag = new Tag(boardId, tagId, "ntut/csie/sslab/ezkanban/kanban/tag", "White");

        tag.rename("issue");

        assertEquals(tagId, tag.getId());
        assertEquals(boardId, tag.getBoardId());
        assertEquals("issue", tag.getName());
        assertEquals("White", tag.getColor());
        assertEquals(2, tag.getDomainEvents().size());
        assertEquals(TagEvents.TagCreated.class, tag.getDomainEvents().get(0).getClass());
        assertEquals(TagEvents.TagRenamed.class, tag.getDomainEvents().get(1).getClass());
    }

    @Test
    public void delete_tag(){
        BoardId boardId = BoardId.create();
        String tagId = UUID.randomUUID().toString();
        Tag tag = new Tag(boardId, tagId, "ntut/csie/sslab/ezkanban/kanban/tag", "Black");

        tag.markAsDeleted("teddy");

        assertTrue(tag.isDeleted());
        assertEquals(2, tag.getDomainEvents().size());
        assertEquals(TagEvents.TagCreated.class, tag.getDomainEvents().get(0).getClass());
        assertEquals(TagEvents.TagDeleted.class, tag.getDomainEvents().get(1).getClass());
    }

    @Test
    public void restore_state_with_event(){
        BoardId boardId = BoardId.create();
        String tagId = UUID.randomUUID().toString();
        TagEvents.TagCreated tagCreated =
                new TagEvents.TagCreated(boardId, tagId, "ntut/csie/sslab/ezkanban/kanban/tag", "Yellow", UUID.randomUUID(), DateProvider.now());
        TagEvents.TagRenamed tagRename =
                new TagEvents.TagRenamed(boardId, tagId, "bug", UUID.randomUUID(), DateProvider.now());

        Tag tag = new Tag(Arrays.asList(tagCreated, tagRename));

        assertEquals(tagId, tag.getId());
        assertEquals(boardId, tag.getBoardId());
        assertEquals("bug", tag.getName());
        assertEquals("Yellow", tag.getColor());
        assertEquals(0, tag.getDomainEvents().size());
    }

    @Test
    public void get_tag_canonical_name_vs_type_mapper_name(){

        assertEquals("ntut.csie.sslab.ezkanban.kanban.tag.entity.TagEvents.TagCreated",
                TagEvents.TagCreated.class.getCanonicalName());

        assertEquals("TagEvents$TagCreated", TagEvents.TypeMapper.TAG_CREATED);
    }
}
