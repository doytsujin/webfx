// Generated by WebFx

module mongoose.client.authn {

    // Direct dependencies modules
    requires mongoose.shared.domainmodel;
    requires webfx.framework.shared.authn;
    requires webfx.framework.shared.orm.domainmodel;
    requires webfx.platform.shared.query;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.client.services.authn;

    // Provided services
    provides webfx.framework.shared.services.authn.spi.AuthenticationServiceProvider with mongoose.client.services.authn.MongooseAuthenticationServiceProvider;

}