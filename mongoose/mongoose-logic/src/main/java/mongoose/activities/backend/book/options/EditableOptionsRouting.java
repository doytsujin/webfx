package mongoose.activities.backend.book.options;

import mongoose.activities.bothends.book.options.OptionsRouting;
import naga.framework.activity.combinations.viewdomain.impl.ViewDomainActivityContextFinal;
import naga.framework.ui.uirouter.UiRoute;

/**
 * @author Bruno Salmon
 */
public class EditableOptionsRouting {

    public static UiRoute<?> uiRoute() {
        return UiRoute.create(OptionsRouting.getPath()
                , false
                , EditableOptionsActivity::new
                , ViewDomainActivityContextFinal::new
        );
    }

}
