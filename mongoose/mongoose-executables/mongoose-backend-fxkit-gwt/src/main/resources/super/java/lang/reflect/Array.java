package java.lang.reflect;

import webfx.platform.shared.services.log.Logger;

public final class Array {

    public static Object newInstance(Class<?> componentType, int length) throws NegativeArraySizeException {
        switch (componentType.getName()) {
            case "webfx.platform.shared.services.query.QueryResult": return new webfx.platform.shared.services.query.QueryResult[length];
            case "webfx.platform.shared.services.update.UpdateResult": return new webfx.platform.shared.services.update.UpdateResult[length];
            // TYPE NOT FOUND
            default:
               Logger.log("GWT super source Array.newInstance() has no case for type " + componentType + ", so new Object[] is returned but this may cause a ClassCastException.");
               return new Object[length];
        }
    }

}