package mongoose.client.entities.util.filters;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import mongoose.client.presentationmodel.HasColumnsStringFilterProperty;
import mongoose.client.presentationmodel.HasConditionStringFilterProperty;
import mongoose.client.presentationmodel.HasGroupStringFilterProperty;
import mongoose.client.presentationmodel.HasSearchTextProperty;
import mongoose.shared.entities.Filter;
import webfx.framework.client.ui.controls.button.EntityButtonSelector;
import webfx.framework.client.ui.layouts.SceneUtil;

import static webfx.framework.client.ui.layouts.LayoutUtil.setHGrowable;
import static webfx.framework.client.ui.layouts.LayoutUtil.setMaxHeightToInfinite;

public final class FilterSearchBar {

    private final EntityButtonSelector<Filter> conditionSelector, groupSelector, columnsSelector;
    private final TextField searchBox; // Keeping this reference to activate focus on activity resume

    public FilterSearchBar(FilterButtonSelectorFactoryMixin mixin, String activityName, String domainClassId, Pane parent, Object pm) {
        if (pm instanceof HasConditionStringFilterProperty)
            conditionSelector = mixin.createConditionFilterButtonSelectorAndBind(activityName,domainClassId, parent, (HasConditionStringFilterProperty) pm);
        else
            conditionSelector = null;
        if (pm instanceof HasGroupStringFilterProperty)
            groupSelector = mixin.createGroupFilterButtonSelectorAndBind(activityName,domainClassId, parent, (HasGroupStringFilterProperty) pm);
        else
            groupSelector = null;
        if (pm instanceof HasColumnsStringFilterProperty)
            columnsSelector = mixin.createColumnsFilterButtonSelectorAndBind(activityName,domainClassId, parent, (HasColumnsStringFilterProperty) pm);
        else
            columnsSelector = null;
        if (pm instanceof HasSearchTextProperty) {
            searchBox = mixin.newTextFieldWithPrompt("GenericSearchPlaceholder");
            ((HasSearchTextProperty) pm).searchTextProperty().bind(searchBox.textProperty());
        } else
            searchBox = null;
    }

    public HBox buildUi() {
        HBox bar = new HBox(10);
        ObservableList<Node> children = bar.getChildren();
        if (conditionSelector != null)
            children.add(conditionSelector.getButton());
        if (groupSelector != null)
            children.add(groupSelector.getButton());
        if (columnsSelector != null)
            children.add(columnsSelector.getButton());
        if (searchBox != null)
            children.add(setMaxHeightToInfinite(setHGrowable(searchBox)));
        return bar;
    }

    public void onResume() {
        if (searchBox != null)
            SceneUtil.autoFocusIfEnabled(searchBox);
    }

}