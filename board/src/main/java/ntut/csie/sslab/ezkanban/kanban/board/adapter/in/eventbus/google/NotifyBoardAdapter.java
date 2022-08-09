package ntut.csie.sslab.ezkanban.kanban.board.adapter.in.eventbus.google;

import com.google.common.eventbus.Subscribe;
import ntut.csie.sslab.ddd.adapter.eventbroker.AbstractNotifyAdapter;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ddd.entity.RemoteDomainEvent;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.board.NotifyBoard;
import ntut.csie.sslab.ezkanban.kanban.workflow.entity.WorkflowEvents;
import org.springframework.beans.factory.annotation.Autowired;

public class NotifyBoardAdapter extends AbstractNotifyAdapter implements Runnable{

    private final NotifyBoard notifyBoard;

    @Autowired
    public NotifyBoardAdapter(NotifyBoard notifyBoard) {
        super(DEFAULT_QUEUE_SIZE);
        this.notifyBoard = notifyBoard;
    }

    @Subscribe
    public void whenWorkflowCreated(WorkflowEvents.WorkflowCreated workflowCreated) {
        putEvent(workflowCreated);
    }

    @Subscribe
    public void whenWorkflowDeleted(WorkflowEvents.WorkflowDeleted workflowDeleted) {
        putEvent(workflowDeleted);
    }

    @Subscribe
    public void whenReceiveRemoteEvent(RemoteDomainEvent remoteDomainEvent) {
        putEvent(remoteDomainEvent);
    }

    @Override
    public void run() {
        while (true) {
            try {
                DomainEvent domainEvent = takeEvent();
                switch(domainEvent) {
                    case WorkflowEvents.WorkflowCreated event -> notifyBoard.whenWorkflowCreated(event);
                    case WorkflowEvents.WorkflowDeleted event -> notifyBoard.whenWorkflowDeleted(event);
                    default -> {}
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
