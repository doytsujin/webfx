// Generated by WebFx

module mongoose.frontend.activities.payment {

    // Direct dependencies modules
    requires java.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.client.aggregates;
    requires mongoose.client.entities;
    requires mongoose.client.sectionpanel;
    requires mongoose.client.util;
    requires mongoose.frontend.activities.cart.routing;
    requires mongoose.shared.domainmodel;
    requires mongoose.shared.entities;
    requires webfx.extras.webtext.controls;
    requires webfx.framework.client.action;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.i18n;
    requires webfx.framework.client.orm.domainmodel.activity;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.client.util;
    requires webfx.framework.shared.orm.entity;
    requires webfx.platform.client.uischeduler;
    requires webfx.platform.client.websocketbus;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.client.windowlocation;
    requires webfx.platform.shared.bus;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.submit;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.frontend.activities.payment;
    exports mongoose.frontend.activities.payment.routing;
    exports mongoose.frontend.operations.payment;

    // Provided services
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.frontend.activities.payment.PaymentUiRoute;

}