package mongoose.activities.backend.letter;

import mongoose.activities.bothends.generic.routing.MongooseRoutingUtil;
import naga.framework.activity.combinations.viewdomain.impl.ViewDomainActivityContextFinal;
import naga.framework.ui.uirouter.UiRoute;

/**
 * @author Bruno Salmon
 */
public class LetterRouting {

    private final static String PATH = "/letter/:letterId";

    public static UiRoute<?> uiRoute() {
        return UiRoute.create(PATH
                , false
                , LetterActivity::new
                , ViewDomainActivityContextFinal::new
        );
    }

    public static String getEditLetterPath(Object letterId) {
        return MongooseRoutingUtil.interpolateLetterIdInPath(letterId, PATH);
    }
}
