// Generated by WebFx

module webfx.platform.shared.json.java {

    // Direct dependencies modules
    requires webfx.platform.shared.json;

    // Exported packages
    exports webfx.platform.shared.services.json.spi.impl.java;

    // Provided services
    provides webfx.platform.shared.services.json.spi.JsonProvider with webfx.platform.shared.services.json.spi.impl.java.JavaJsonProvider;

}