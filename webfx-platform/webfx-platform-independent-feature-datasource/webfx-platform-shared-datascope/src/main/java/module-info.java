// Generated by WebFx

module webfx.platform.shared.datascope {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.serial;

    // Exported packages
    exports webfx.platform.shared.datascope;
    exports webfx.platform.shared.datascope.aggregate;
    exports webfx.platform.shared.datascope.schema;

    // Provided services
    provides webfx.platform.shared.services.serial.spi.SerialCodec with webfx.platform.shared.datascope.aggregate.AggregateScope.ProvidedSerialCodec;

}