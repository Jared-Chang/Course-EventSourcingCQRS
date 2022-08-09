package ntut.csie.sslab.ezkanban.kanban.board.adapter.out.repository.springboot.getcontent;

import ntut.csie.sslab.ddd.entity.common.Json;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardMember;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.out.repository.BoardMemberDto;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.getcontent.*;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardDto;
import ntut.csie.sslab.ezkanban.kanban.card.usecase.port.out.repository.CardMapper;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserDto;
import ntut.csie.sslab.ezkanban.kanban.user.usecase.out.repository.UserRepository;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.LaneId;
import ntut.csie.sslab.ezkanban.kanban.workflow.usecase.port.out.repository.WorkflowMapper;

import java.util.*;

import static ntut.csie.sslab.ddd.entity.common.Contract.requireNotNull;

public class BoardContentReadModelRepositoryImpl implements BoardContentReadModelRepository {

    private final BoardContentReadModelRepositoryPeer peer;
    private final UserRepository userRepository;

    public BoardContentReadModelRepositoryImpl(BoardContentReadModelRepositoryPeer peer,
                                               UserRepository userRepository) {
        requireNotNull("BoardContentReadModelRepositoryPeer", peer);
        requireNotNull("UserRepository", userRepository);

        this.peer = peer;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<BoardContentViewModel> findById(String boardId) {
        Optional<BoardContentViewData> model = peer.findById(boardId);
        if (model.isPresent()) {
            BoardContentState state = Json.readValue(model.get().getViewModel(), BoardContentState.class);
            BoardContentViewModel viewModel = new BoardContentViewModel(state.boardState().boardId().id(), state.boardState().name());
            viewModel.setWorkflows(WorkflowMapper.toDto(state.workflowStates()));
            List<BoardMemberDto> boardMemberDtos = new ArrayList<>();
            for (BoardMember boardMember : state.boardState().boardMembers()) {
                UserDto userDto = userRepository.findById(boardMember.getUserId()).get();
                boardMemberDtos.add(new BoardMemberDto(boardMember.getBoardId().id(), userDto, boardMember.getBoardRole()));
            }
            viewModel.setBoardMembers(boardMemberDtos);
            viewModel.setCommittedCards(getCommittedCards(state.committedCardStates()));
            viewModel.setBoardVersion(state.boardVersion());
            return Optional.of(viewModel);
        }
        return Optional.empty();
    }

    @Override
    public void save(BoardContentViewModel model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(BoardContentViewModel data) {
        throw new UnsupportedOperationException();
    }

    private Map<String, List<CardDto>> getCommittedCards(Map<LaneId, List<CardState>> cardStates) {

        Map<String, List<CardDto>> committedCards = new HashMap<>();

        for (var laneId : cardStates.keySet()) {
            List<CardDto> cardDtos = cardStates.get(laneId).stream().map(CardMapper::toDto).toList();
            committedCards.put(laneId.id(), cardDtos);
        }

        return committedCards;
    }

    @Override
    public Optional<BoardContentViewModel> getBoardContent(String boardId, Date endDate) {
        return findById(boardId);
    }

}
