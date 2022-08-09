fromStreams(["$ce-Card", "$ce-Tag", "$ce-Workflow", "$ce-Board"])
.when({
    $init: function(){
        return {
        }
    },
    $any: function(s,e) {
        linkTo("GetBoardContentUseCase-by-Board-" + e.body["boardId"]["id"], e);
    },
});