package naga.platform.json;

import naga.platform.json.listmap.MapJsonObject;
import naga.platform.json.spi.JsonArray;
import naga.platform.json.spi.JsonProvider;
import naga.platform.json.spi.WritableJsonArray;
import naga.platform.json.spi.WritableJsonObject;
import naga.util.serviceloader.ServiceLoaderHelper;

/**
 * @author Bruno Salmon
 */
public class Json {

    /***************************
     * Factory methods helpers *
     **************************/

    public static WritableJsonObject createObject() {
        return getFactory().createJsonObject();
    }

    public static <NO> WritableJsonObject createObject(NO nativeObject) {
        return getFactory().nativeToJavaJsonObject(nativeObject);
    }

    public static WritableJsonArray createArray() {
        return getFactory().createJsonArray();
    }

    public static <NA> WritableJsonArray createArray(NA nativeArray) {
        return getFactory().nativeToJavaJsonArray(nativeArray);
    }

    public static WritableJsonObject parseObject(String text) {
        return getFactory().parseObject(text);
    }

    public static WritableJsonArray parseArray(String text) {
        return getFactory().parseArray(text);
    }

    public static Object javaToNativeScalar(Object scalar) {
        return getFactory().javaToNativeScalar(scalar);
    }


    private static JsonProvider PROVIDER;

    public static void registerProvider(JsonProvider factory) {
        PROVIDER = factory;
    }

    public static JsonProvider getFactory() {
        if (PROVIDER == null) {
            registerProvider(ServiceLoaderHelper.loadService(JsonProvider.class, ServiceLoaderHelper.NotFoundPolicy.TRACE_AND_RETURN_NULL));
            if (PROVIDER == null) {
                System.out.println("Using default built-in JSON factory which is not interoperable with the underlying platform! Be sure you haven't forget to call Json.registerFactory().");
                PROVIDER = new MapJsonObject();
            }
        }
        return PROVIDER;
    }

    /***********************************
     * Java conversion methods helpers *
     **********************************/

    public static <T> WritableJsonArray fromJavaArray(T[] javaArray) {
        if (javaArray == null)
            return null;
        WritableJsonArray valuesArray = createArray();
        for (Object javaValue : javaArray)
            valuesArray.push(javaValue);
        return valuesArray;
    }

    public static Object[] toJavaArray(JsonArray jsonArray) {
        if (jsonArray == null)
            return null;
        int length = jsonArray.size();
        Object[] javaArray = new Object[length];
        for (int i = 0; i < length; i++)
            javaArray[i] = jsonArray.getElement(i);
        return javaArray;
    }

}
