package ntut.csie.sslab.ezkanban.kanban.common.usecase;

import com.google.common.eventbus.Subscribe;
import ntut.csie.sslab.ddd.entity.DomainEvent;
import ntut.csie.sslab.ezkanban.kanban.board.usecase.port.in.notify.board.NotifyBoard;

public class AllEventsListener {

    public int notifyCount = 0;

    @Subscribe
    public void when(DomainEvent event) {
        notifyCount++;
        System.out.println("AllEventsListener: " + event);
    }

}
