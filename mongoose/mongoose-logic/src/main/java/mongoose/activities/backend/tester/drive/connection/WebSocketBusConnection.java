package mongoose.activities.backend.tester.drive.connection;

import mongoose.activities.backend.tester.drive.command.Command;
import mongoose.activities.backend.monitor.listener.ConnectionEvent;
import mongoose.activities.backend.monitor.listener.EventType;
import naga.platform.bus.call.BusCallService;
import naga.platform.client.bus.WebSocketBus;
import naga.platform.json.spi.JsonObject;
import naga.platform.client.websocket.WebSocketListener;
import naga.platform.services.log.spi.Logger;
import naga.platform.spi.Platform;

/**
 * @author Jean-Pierre Alonso.
 */
public class WebSocketBusConnection extends ConnectionBase {
    protected WebSocketBus bus;
    boolean opened;

    @Override
    public ConnectionEvent executeCommand(Command t) {
        ConnectionEvent event;
        switch (t) {
            case OPEN:
                bus = (WebSocketBus)Platform.createBus();
                bus.setWebSocketListener(createWebSocketListener());
                event = new ConnectionEvent(EventType.CONNECTING);
                long t0 = System.currentTimeMillis();
                BusCallService.call("version", "ignored", bus).setHandler(asyncResult -> {
                    long t1 = System.currentTimeMillis();
                    String message = (asyncResult.succeeded() ? "Connected : "+bus.toString() : "Error : " + asyncResult.cause()) + " in " + (t1 - t0) + "ms";
                    Logger.log(message);
                });
                break;
            case CLOSE:
                event = new ConnectionEvent(EventType.DISCONNECTING);
                bus.close();
                break;
            default:
                event = null;
        }
        applyEvent(event);
        recordEvent(event);
        return event;
    }

    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen() {
                opened = true;
                ConnectionEvent event1 = new ConnectionEvent(EventType.CONNECTED);
                applyEvent(event1);
                recordEvent(event1);
                Logger.log("Cnx-CONNECTED : "+bus.toString());
            }

            @Override
            public void onClose(JsonObject reason) {
                if (opened) {
                    ConnectionEvent event1 = new ConnectionEvent(EventType.NOT_CONNECTED);
                    applyEvent(event1);
                    recordEvent(event1);
                    opened = false;
                }
                Logger.log("Cnx-CLOSED : "+reason);
            }

            @Override
            public void onMessage(String message) {
//                Platform.log("Cnx-MESSAGE : "+message);
            }

            @Override
            public void onError(String error) {
                Logger.log("Cnx-ERROR ("+bus.toString()+") : "+error);
            }
        };
    }
}
