package naga.core.type;

/*
 * @author Bruno Salmon
 */

public class ObjectType implements Type {

    protected final Class objectClass;

    public ObjectType(Class objectClass) {
        this.objectClass = objectClass;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    // static

    public static ObjectType fromClass(Class objectClass) {
        return new ObjectType(objectClass);
    }

    public static ObjectType fromObject(Object o) {
        return fromClass(o.getClass());
    }

}
