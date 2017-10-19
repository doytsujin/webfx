package naga.framework.router.impl;

import naga.framework.session.Session;
import naga.platform.json.Json;
import naga.platform.json.spi.JsonObject;
import naga.platform.json.spi.WritableJsonObject;
import naga.framework.router.Route;
import naga.framework.router.RoutingContext;
import naga.platform.services.auth.spi.User;
import naga.platform.services.log.spi.Logger;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Bruno Salmon
 */
abstract class RoutingContextImplBase implements RoutingContext {

    protected final String mountPoint;
    protected final String path;
    protected final Collection<RouteImpl> routes;
    protected Iterator<RouteImpl> iter;
    protected Route currentRoute;
    private WritableJsonObject params;
    private Session session;
    private User user;

    RoutingContextImplBase(String mountPoint, String path, Collection<RouteImpl> routes, JsonObject state) {
        this.mountPoint = mountPoint;
        this.path = path;
        this.routes = routes;
        this.params = (WritableJsonObject) state; // Is merging state and params the right thing to do?
        iter = routes.iterator();
    }

    @Override
    public String mountPoint() {
        return mountPoint;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public Route currentRoute() {
        return currentRoute;
    }

    @Override
    public void next() {
        iterateNext();
    }

    boolean iterateNext() {
        boolean failed = failed();
        while (iter.hasNext()) {
            RouteImpl route = iter.next();
            if (route.matches(this, mountPoint(), failed)) {
                //Platform.log("Route matches: " + route);
                try {
                    currentRoute = route;
                    //Platform.log("Calling the " + (failed ? "failure" : "") + " handler");
                    if (failed)
                        route.handleFailure(this);
                    else
                        route.handleContext(this);
                } catch (Throwable t) {
                    Logger.log("Throwable thrown from handler", t);
                    if (!failed) {
                        Logger.log("Failing the routing");
                        fail(t);
                    } else {
                        // Failure in handling failure!
                        Logger.log("Failure in handling failure");
                        unhandledFailure(-1, t, route.router());
                    }
                } finally {
                    currentRoute = null;
                }
                return true;
            }
        }
        return false;
    }

    void unhandledFailure(int statusCode, Throwable failure, RouterImpl router) {
        //int code = statusCode != -1 ? statusCode : 500;
        if (failure != null) {
            if (router.exceptionHandler() != null)
                router.exceptionHandler().handle(failure);
            else
                Logger.log("Unexpected exception in route", failure);
        }
        /*if (!response().ended()) {
            try {
                response().setStatusCode(code);
            } catch (IllegalArgumentException e) {
                // means that there are invalid chars in the status message
                response()
                        .setStatusMessage(HttpResponseStatus.valueOf(code).reasonPhrase())
                        .setStatusCode(code);
            }
            response().end(response().getStatusMessage());
        }*/
    }

    @Override
    public WritableJsonObject getParams() {
        if (params == null)
            params = Json.createObject();
        return params;
    }

    @Override
    public Session session() {
        return session;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void clearUser() {
        setUser(null);
    }
}
