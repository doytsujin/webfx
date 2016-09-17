package naga.framework.activity.client;

import javafx.beans.property.Property;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.router.UiRouter;
import naga.platform.activity.ActivityContextDirectAccess;
import naga.platform.client.url.history.History;
import naga.platform.json.spi.JsonObject;
import naga.toolkit.spi.nodes.GuiNode;

/**
 * @author Bruno Salmon
 */
public interface UiActivityContextDirectAccess<C extends UiActivityContext<C>> extends ActivityContextDirectAccess<C>, UiActivityContext<C> {

    @Override
    default UiRouter getUiRouter() { return getActivityContext().getUiRouter(); }

    @Override
    default History getHistory() { return getActivityContext().getHistory(); }

    @Override
    default JsonObject getParams() { return getActivityContext().getParams(); }

    @Override
    default <T> T getParameter(String key) {
        return getActivityContext().getParameter(key);
    }

    @Override
    default Property<GuiNode> nodeProperty() { return getActivityContext().nodeProperty(); }

    @Override
    default Property<GuiNode> mountNodeProperty() { return getActivityContext().mountNodeProperty(); }

    @Override
    default I18n getI18n() { return getActivityContext().getI18n(); }
}
