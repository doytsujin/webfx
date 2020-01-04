// Generated by WebFx

module webfx.extras.webtext.controls.peers.javafx {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.web;
    requires jdk.jsobject;
    requires webfx.extras.webtext.controls;
    requires webfx.extras.webtext.controls.peers.base;
    requires webfx.extras.webtext.controls.registry;
    requires webfx.kit.javafx;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.extras.webtext.controls.peers.javafx;
    exports webfx.extras.webtext.controls.registry.spi.impl.javafx;

    // Provided services
    provides webfx.extras.webtext.controls.registry.spi.WebTextRegistryProvider with webfx.extras.webtext.controls.registry.spi.impl.javafx.JavaFxWebTextRegistryProvider;

}