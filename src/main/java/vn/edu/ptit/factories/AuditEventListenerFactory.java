package vn.edu.ptit.factories;


import io.trino.spi.eventlistener.EventListener;
import io.trino.spi.eventlistener.EventListenerFactory;
import vn.edu.ptit.listeners.AuditEventListener;

import java.io.IOException;
import java.util.Map;

public class AuditEventListenerFactory implements EventListenerFactory {

    @Override
    public String getName() {
        return "audit-event-listener";
    }

    @Override
    public EventListener create(Map<String, String> config) {
        try {
            return new AuditEventListener(config);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
