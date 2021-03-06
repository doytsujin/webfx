// Generated by WebFx

module mongoose.frontend.activities.startbooking {

    // Direct dependencies modules
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.client.bookingprocess;
    requires mongoose.client.entities;
    requires mongoose.client.icons;
    requires mongoose.client.util;
    requires mongoose.frontend.activities.fees;
    requires mongoose.frontend.activities.options;
    requires mongoose.frontend.activities.program;
    requires mongoose.frontend.activities.terms;
    requires mongoose.shared.entities;
    requires webfx.extras.imagestore;
    requires webfx.framework.client.action;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.orm.domainmodel.activity;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.client.util;
    requires webfx.kit.util;
    requires webfx.platform.client.uischeduler;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.frontend.activities.startbooking;
    exports mongoose.frontend.activities.startbooking.routing;
    exports mongoose.frontend.operations.startbooking;

    // Provided services
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.frontend.activities.startbooking.StartBookingUiRoute;

}