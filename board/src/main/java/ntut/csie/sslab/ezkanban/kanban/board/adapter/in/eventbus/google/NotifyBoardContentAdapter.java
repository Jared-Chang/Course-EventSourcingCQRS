package ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google;

import com.google.common.eventbus.Subscribe;
import ntut.csie.sslab.ddd.adapter.eventbroker.AbstractNotifyAdapter;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ezkanban.kanban.board.entity.BoardEvents;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.boardcontent.NotifyBoardContent;
import ntut.csie.sslab.ezkanban.kanban.card.entity.CardEvents;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;
import org.springframework.stereotype.Component;

@Component
public class NotifyBoardContentAdapter extends AbstractNotifyAdapter implements Runnable {
    private NotifyBoardContent notifyBoardContent;

    public NotifyBoardContentAdapter(NotifyBoardContent notifyBoardContent) {
        super(DEFAULT_QUEUE_SIZE);
        this.notifyBoardContent = notifyBoardContent;
    }

    @Subscribe
    public void whenBoardEvents(BoardEvents boardEvent) {
        putEvent(boardEvent);
    }

    @Subscribe
    public void whenWorkflowEvents(WorkflowEvents workflowEvent) {

        putEvent(workflowEvent);
    }

    @Subscribe
    public void whenCardEvents(CardEvents cardEvents) {
        putEvent(cardEvents);
    }

    @Override
    public void run() {
        while (true) {
            try {

                DomainEvent domainEvent = takeEvent();
                notifyBoardContent.project(domainEvent);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
