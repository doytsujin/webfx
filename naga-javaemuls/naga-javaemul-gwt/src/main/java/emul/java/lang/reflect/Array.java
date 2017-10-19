package emul.java.lang.reflect;

import naga.platform.services.log.spi.Logger;
import naga.platform.services.query.QueryResultSet;
import naga.platform.services.update.UpdateResult;

public final class Array {

    public static Object newInstance(Class<?> componentType, int length) throws NegativeArraySizeException {
        if (componentType.equals(Object.class)) return new Object[length];
        if (componentType.equals(QueryResultSet.class)) return new QueryResultSet[length];
        if (componentType.equals(UpdateResult.class)) return new UpdateResult[length];
        Logger.log("GWT super source Array.newInstance() has no case for type " + componentType + ", so new Object[] is returned but this may cause a ClassCastException.");
        return new Object[length];
    }

}