package naga.core.activity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.core.json.JsonObject;
import naga.core.orm.domainmodel.DataSourceModel;
import naga.core.routing.history.History;
import naga.core.spi.toolkit.GuiNode;
import naga.core.spi.toolkit.hasproperties.HasNodeProperty;

/**
 * @author Bruno Salmon
 */
public class ActivityContext implements HasNodeProperty {

    private final ActivityContext parentContext;
    private JsonObject params;
    private ActivityManager activityManager;
    private DataSourceModel dataSourceModel;
    private History history;

    public ActivityContext(ActivityContext parentContext) {
        this.parentContext = parentContext;
    }

    public JsonObject getParams() {
        return params;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public DataSourceModel getDataSourceModel() {
        return dataSourceModel != null || parentContext == null ? dataSourceModel : parentContext.getDataSourceModel();
    }

    void setDataSourceModel(DataSourceModel dataSourceModel) {
        this.dataSourceModel = dataSourceModel;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public History getHistory() {
        return history != null || parentContext == null ? history : parentContext.getHistory();
    }

    private Property<GuiNode> nodeProperty;
    public Property<GuiNode> nodeProperty() {
        if (nodeProperty == null)
            nodeProperty = new SimpleObjectProperty<>();
        return nodeProperty;
    }

    private Property<GuiNode> mountNodeProperty;
    public Property<GuiNode> mountNodeProperty() {
        if (mountNodeProperty == null)
            mountNodeProperty = new SimpleObjectProperty<>();
        return mountNodeProperty;
    }
}
