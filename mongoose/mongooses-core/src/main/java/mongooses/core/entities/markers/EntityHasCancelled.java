package mongooses.core.entities.markers;

import webfx.framework.orm.entity.Entity;

/**
 * @author Bruno Salmon
 */
public interface EntityHasCancelled extends Entity, HasCancelled {

    @Override
    default void setCancelled(Boolean cancelled) {
        setFieldValue("cancelled", cancelled);
    }

    @Override
    default Boolean isCancelled() {
        return getBooleanFieldValue("cancelled");
    }

}