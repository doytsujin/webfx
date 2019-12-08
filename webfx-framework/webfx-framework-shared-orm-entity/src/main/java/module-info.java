// Generated by WebFx

module webfx.framework.shared.orm.entity {

    // Direct dependencies modules
    requires java.base;
    requires webfx.extras.type;
    requires webfx.framework.shared.orm.domainmodel;
    requires webfx.framework.shared.orm.dql;
    requires webfx.framework.shared.orm.expression;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.query;
    requires webfx.platform.shared.update;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.framework.shared.orm.entity;
    exports webfx.framework.shared.orm.entity.impl;
    exports webfx.framework.shared.orm.entity.lciimpl;
    exports webfx.framework.shared.orm.entity.query_result_to_entities;
    exports webfx.framework.shared.orm.entity.result;
    exports webfx.framework.shared.orm.entity.result.impl;

    // Used services
    uses webfx.framework.shared.orm.entity.EntityFactoryProvider;

}