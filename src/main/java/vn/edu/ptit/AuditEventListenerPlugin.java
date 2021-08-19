package vn.edu.ptit;


import io.trino.spi.Plugin;
import io.trino.spi.eventlistener.EventListenerFactory;
import vn.edu.ptit.factories.AuditEventListenerFactory;

import java.util.List;

public class AuditEventListenerPlugin implements Plugin {

    @Override
    public Iterable<EventListenerFactory> getEventListenerFactories() {
        return List.of(new AuditEventListenerFactory());
    }

}
