// Generated by WebFx

module webfx.platform.shared.buscalljob {

    // Direct dependencies modules
    requires webfx.platform.shared.appcontainer;
    requires webfx.platform.shared.bus;
    requires webfx.platform.shared.buscall;
    requires webfx.platform.shared.log;

    // Exported packages
    exports webfx.platform.server.jobs.buscall;

    // Provided services
    provides webfx.platform.shared.services.appcontainer.spi.ApplicationJob with webfx.platform.server.jobs.buscall.BusCallServerJob;

}