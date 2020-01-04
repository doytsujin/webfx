// Generated by WebFx

module webfx.platform.client.websocketbus {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.client.websocket;
    requires webfx.platform.shared.bus;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.resource;
    requires webfx.platform.shared.scheduler;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.platform.client.services.websocketbus;

    // Provided services
    provides webfx.platform.shared.services.bus.spi.BusServiceProvider with webfx.platform.client.services.websocketbus.WebsocketBusServiceProvider;

}