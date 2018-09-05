package mongooses.web.activities.sharedends;

import com.google.gwt.core.client.EntryPoint;
import mongooses.core.activities.sharedends.MongooseSharedEndsApplication;
import webfx.platform.gwt.GwtPlatform;

/**
 * @author Bruno Salmon
 */
public abstract class MongooseSharedEndsWebApplication implements EntryPoint {

    /* No need for GwtPlatform.register(); as the platform will be found by the customized ServiceLoader provided in the super-source */

    @Override
    public void onModuleLoad() {
        registerResourceBundles();
        registerCustomViewBuilders();
        startMongooseApplicationLogic();
        MongooseSharedEndsApplication.setLoadingSpinnerVisibleConsumer(MongooseSharedEndsWebApplication::setLoadingSpinnerVisible);
    }

    protected abstract void startMongooseApplicationLogic();

    protected void registerResourceBundles() {
        GwtPlatform.registerBundle(MongooseSharedEndsWebBundle.B);
    }

    protected void registerCustomViewBuilders() {}

    private static native void setLoadingSpinnerVisible(boolean visible) /*-{
        var loadingSpinner = $wnd.document.getElementById("loadingSpinner");
        if (!loadingSpinner) {
            if (!visible)
                return;
            loadingSpinner = $wnd.document.createElement("table");
            loadingSpinner.setAttribute("id", "loadingSpinner");
            loadingSpinner.innerHTML = '<tr> <td style="text-align: center; vertical-align: middle;"> <div class="loader"> <svg class="circular"> <circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"></circle> </svg> </div> </td> </tr>';
            $wnd.document.body.insertBefore(loadingSpinner, $wnd.document.body.firstChild);
        }
        loadingSpinner.setAttribute("style", "visibility: " + (visible ? "visible" : "hidden"));
    }-*/;

}