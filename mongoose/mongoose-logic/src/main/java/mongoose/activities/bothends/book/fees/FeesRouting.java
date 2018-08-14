package mongoose.activities.bothends.book.fees;

import mongoose.activities.bothends.generic.routing.MongooseRoutingUtil;
import naga.framework.activity.combinations.viewdomain.impl.ViewDomainActivityContextFinal;
import naga.framework.ui.uirouter.UiRoute;

/**
 * @author Bruno Salmon
 */
public class FeesRouting {

    private final static String PATH = "/book/event/:eventId/fees";

    public static UiRoute<?> uiRoute() {
        return UiRoute.create(PATH
                , false
                , FeesActivity::new
                , ViewDomainActivityContextFinal::new
        );
    }

    public static String getFeesPath(Object eventId) {
        return MongooseRoutingUtil.interpolateEventIdInPath(eventId, PATH);
    }
}
