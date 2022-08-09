package ntut.csie.sslab.ezkanban.kanban.board.usecase.service;


import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.common.DateProvider;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardMember;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardMemberBuilder;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentState;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.BoardContentStateRepository;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.CardState;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.WorkflowState;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.boardcontent.NotifyBoardContent;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardId;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.*;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;


public class NotifyBoardContentService implements NotifyBoardContent {
    private final BoardContentStateRepository boardContentStateRepository;

    private final String id;

    public NotifyBoardContentService(String id, BoardContentStateRepository boardContentStateRepository) {
        this.id = id;
        this.boardContentStateRepository = boardContentStateRepository;
    }

    @Override
    public void project(DomainEvent domainEvent) {
        BoardContentState boardContentState = null;

        if (boardContentStateRepository.isEventHandled(id, domainEvent.id().toString())){
            return;
        }

        switch (domainEvent) {
            case BoardEvents.BoardCreated event -> {
                boardContentState = BoardContentState.create();
                boardContentState.boardState().isDeleted(false);
                boardContentState.boardState().boardId(event.boardId());
                boardContentState.boardState().teamId(event.teamId());
                boardContentState.boardState().name(event.boardName());
            }
            case BoardEvents.BoardMemberAdded event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                BoardMember boardMember = BoardMemberBuilder.newInstance()
                        .memberType(event.boardRole())
                        .boardId(event.boardId())
                        .userId(event.userId())
                        .build();
                boardContentState.boardState().boardMembers().add(boardMember);
            }
            case WorkflowEvents.WorkflowCreated event -> {
                WorkflowState workflowState = WorkflowState.create();
                workflowState.isDeleted(false);
                workflowState.workflowId(event.workflowId());
                workflowState.boardId(event.boardId());
                workflowState.name(event.workflowName());
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                boardContentState.workflowStates().add(workflowState);
            }
            case WorkflowEvents.WorkflowDeleted event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                boardContentState.workflowStates().removeIf(x -> x.workflowId().equals(event.workflowId()));
            }
            case WorkflowEvents.StageCreated event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                WorkflowState workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(event.workflowId())).findAny().get();
                if (event.parentId().isNull()){
                    Lane stage = LaneBuilder.newInstance()
                            .workflowId(event.workflowId())
                            .parentId(NullLane.ID)
                            .laneId(event.stageId())
                            .name(event.name())
                            .wipLimit(event.wipLimit())
                            .type(event.type())
                            .stage()
                            .order(event.order())
                            .build();
                    insertRootStage(workflowState, stage, stage.getOrder());
                }
                else {
                    Lane parentLane = getLaneById(workflowState, event.parentId()).get();
                    parentLane.createStage(event.stageId(), event.name(), event.wipLimit(), event.type());
                }
            }
            case WorkflowEvents.SwimLaneCreated event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                WorkflowState workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(event.workflowId())).findAny().get();
                Lane lane = getLaneById(workflowState, event.parentId()).get();
                lane.createSwimLane(event.swimLaneId(), event.name(), event.wipLimit(), event.type());
            }
            case WorkflowEvents.WorkflowMoved event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                getWorkflowStateAndIncVersion(boardContentState, event.workflowId());
                List<WorkflowState> workflowStates = boardContentState.workflowStates();
                WorkflowState targetWorkflowState = workflowStates.stream().filter(x->x.workflowId().equals(event.workflowId())).findAny().get();

                workflowStates.remove(targetWorkflowState);
                int order = event.order();
                if(event.order() > workflowStates.size()){
                    order = workflowStates.size();
                }

                workflowStates.add(order, targetWorkflowState);
            }
            case CardEvents.CardCreated event -> {
                CardState cardState = CardState.create();
                cardState.isDeleted(false);
                cardState.cardId(event.cardId());
                cardState.boardId(event.boardId());
                cardState.workflowId(event.workflowId());
                cardState.laneId(event.laneId());
                cardState.userId(event.userId());
                cardState.description(event.description());

                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                if (boardContentState.committedCardStates().containsKey(event.laneId())) {
                    boardContentState.committedCardStates().get(event.laneId()).add(cardState);
                } else {
                    List cards = new LinkedList();
                    cards.add(cardState);
                    boardContentState.committedCardStates().put(event.laneId(), cards);
                }
            }
            case CardEvents.CardDeleted event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                CardState cardState = boardContentState.committedCardStates().get(event.laneId()).stream().filter(x -> x.cardId().equals(event.cardId())).findFirst().get();
                boardContentState.committedCardStates().get(event.laneId()).remove(cardState);
                removeEmptyLaneFromCommittedCardStates(event.laneId(), boardContentState.committedCardStates());
            }
            case CardEvents.CardMoved event -> {
                boardContentState = boardContentStateRepository.findById(event.boardId().id()).get();
                CardState cardState = getCardStateAndIncVersion(boardContentState, event.cardId());
                cardState.laneId(event.newLaneId());

                Map<LaneId, List<CardState>> committedCardStates = boardContentState.committedCardStates();
                CardState targetCardState = committedCardStates.get(event.oldLaneId()).stream().filter(x -> x.cardId().equals(event.cardId())).findFirst().get();

                committedCardStates.get(event.oldLaneId()).remove(targetCardState);
                if (!committedCardStates.containsKey(event.newLaneId())) {
                    committedCardStates.put(event.newLaneId(), new LinkedList<>());
                }
                int order = event.order() > committedCardStates.get(event.newLaneId()).size() ? committedCardStates.get(event.newLaneId()).size() : event.order();
                removeEmptyLaneFromCommittedCardStates(event.oldLaneId(), committedCardStates);
                committedCardStates.get(event.newLaneId()).add(order, targetCardState);
            }
            default -> {
                return;
            }
        }

        requireNotNull("BoardContentState", boardContentState);

        setIdempotentData(boardContentState, domainEvent);
        boardContentStateRepository.save(boardContentState);
    }

    private void insertRootStage(WorkflowState data, Lane lane, int order) {
        data.rootStages().add(order, lane);
        lane.setParentId(NullLane.nullLane.getId());
        reorderRootStage(data);
    }

    private void reorderRootStage(WorkflowState data) {
        for(int i = 0; i < data.rootStages().size() ; i++){
            data.rootStages().get(i).setOrder(i);
        }
    }

    public Optional<Lane> getLaneById(WorkflowState data, LaneId laneId) {
        requireNotNull("Lane id", laneId);

        Optional<Lane> targetLane;
        for (var stage: data.rootStages()) {
            targetLane = stage.getLaneById(laneId);
            if(targetLane.isPresent()) {
                return targetLane;
            }
        }
        return Optional.empty();
    }

    private void removeEmptyLaneFromCommittedCardStates(LaneId laneId, Map<LaneId, List<CardState>> committedCardStates) {
        if (committedCardStates.get(laneId).isEmpty()) {
            committedCardStates.remove(laneId);
        }
    }

    private static int indexOf(List<WorkflowDto> workflowDtos, WorkflowId workflowId) {
        Optional<WorkflowDto> workflowDto = workflowDtos.stream().filter(x -> x.getWorkflowId().equals(workflowId.id())).findFirst();
        if (workflowDto.isPresent())
            return workflowDtos.indexOf(workflowDto.get());
        throw new RuntimeException("Workflow not found, workflowId: " + workflowId.id());
    }


    private WorkflowState getWorkflowStateAndIncVersion(BoardContentState boardContentState, WorkflowId workflowId) {
        var workflowState = boardContentState.workflowStates().stream().filter(x -> x.workflowId().equals(workflowId)).findAny().get();
        workflowState.incVersion();
        return workflowState;
    }

    private CardState getCardStateAndIncVersion(BoardContentState state, CardId cardId) {
        Optional<CardState> cardState;
        for (List<CardState> cardStateList : state.committedCardStates().values()) {
            cardState = cardStateList.stream().filter(x -> x.cardId().equals(cardId)).findAny();
            if (cardState.isPresent()) {
                cardState.get().incVersion();
                return cardState.get();
            }
        }
        throw new RuntimeException("getCardStateAndIncVersion failed, card not found: " + cardId);
    }

    private void setIdempotentData(BoardContentState boardContentState, DomainEvent event){
        boardContentState.idempotentData().setHandlerId(this.id);
        boardContentState.idempotentData().setEventId(event.id().toString());
        boardContentState.idempotentData().setHandledOn(DateProvider.now());
    }
}

