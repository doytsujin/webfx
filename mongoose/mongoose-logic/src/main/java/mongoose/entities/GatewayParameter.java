package mongoose.entities;

import mongoose.entities.markers.EntityHasName;
import naga.framework.orm.entity.Entity;

/**
 * @author Bruno Salmon
 */
public interface GatewayParameter extends Entity, EntityHasName {

    default void setValue(String value) {
        setFieldValue("value", value);
    }

    default String getValue() {
        return getStringFieldValue("value");
    }
    
    default void setTest(Boolean test) {
        setFieldValue("test", test);
    }

    default Boolean isTest() {
        return getBooleanFieldValue("test");
    }

    default void setLive(Boolean live) {
        setFieldValue("live", live);
    }

    default Boolean isLive() {
        return getBooleanFieldValue("live");
    }

}
