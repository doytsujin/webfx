package naga.framework.ui.action;

import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Bruno Salmon
 */
public interface ActionProducer extends StandardActionKeys {

    ActionBuilder newActionBuilder(Object actionKey);

    default Action newAction(Object actionKey, EventHandler<ActionEvent> actionHandler) {
        return newAuthAction(actionKey, actionHandler, null);
    }

    default Action newAuthAction(Object actionKey, EventHandler<ActionEvent> actionHandler, ObservableBooleanValue authorizedProperty) {
        return newActionBuilder(actionKey).setActionHandler(actionHandler).setAuthorizedProperty(authorizedProperty).build();
    }

    // Same API but with Runnable

    default Action newAction(Object actionKey, Runnable actionHandler) {
        return newAction(actionKey, e -> actionHandler.run());
    }

    default Action newAuthAction(Object actionKey, Runnable actionHandler, ObservableBooleanValue authorizedProperty) {
        return newAuthAction(actionKey, e -> actionHandler.run(), authorizedProperty);
    }

    default Action newOkAction(Runnable handler) {
        return newAction(OK_ACTION_KEY, handler);
    }

    default Action newCancelAction(Runnable handler) {
        return newAction(CANCEL_ACTION_KEY, handler);
    }

    default Action newSaveAction(Runnable handler) {
        return newAction(SAVE_ACTION_KEY, handler);
    }

    default Action newRevertAction(Runnable handler) {
        return newAction(REVERT_ACTION_KEY, handler);
    }

    default Action newAddAction(Runnable handler) {
        return newAction(ADD_ACTION_KEY, handler);
    }

    default Action newRemoveAction(Runnable handler) {
        return newAction(REMOVE_ACTION_KEY, handler);
    }

}