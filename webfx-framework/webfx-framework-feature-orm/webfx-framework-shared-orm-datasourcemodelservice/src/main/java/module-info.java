// Generated by WebFx

module webfx.framework.shared.orm.datasourcemodelservice {

    // Direct dependencies modules
    requires java.base;
    requires webfx.framework.shared.orm.domainmodel;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.submit;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.framework.shared.services.datasourcemodel;
    exports webfx.framework.shared.services.datasourcemodel.spi;

    // Used services
    uses webfx.framework.shared.services.datasourcemodel.spi.DataSourceModelProvider;

}