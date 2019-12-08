// Generated by WebFx

module mongoose.backend.activities.statements {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.backend.masterslave;
    requires mongoose.client.activity;
    requires mongoose.client.presentationmodel;
    requires mongoose.client.util;
    requires mongoose.shared.domainmodel;
    requires mongoose.shared.entities;
    requires webfx.extras.visual;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.activity.domain;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.orm.reactive.dql;
    requires webfx.framework.client.orm.reactive.visual;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.orm.dql;
    requires webfx.framework.shared.orm.entity;
    requires webfx.framework.shared.orm.expression;
    requires webfx.framework.shared.router;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.statements;
    exports mongoose.backend.activities.statements.routing;
    exports mongoose.backend.operations.routes.statements;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.statements.RouteToStatementsRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.statements.StatementsUiRoute;

}