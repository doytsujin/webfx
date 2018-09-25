package webfx.framework.expression.terms;

import webfx.framework.expression.lci.DataReader;
import webfx.fxkits.extra.type.PrimType;
import webfx.fxkits.extra.type.Type;

/**
 * @author Bruno Salmon
 */
public class Exists extends SelectExpression {

    public Exists(Select select) {
        super(select);
    }

    @Override
    public Type getType() {
        return PrimType.BOOLEAN;
    }

    @Override
    public Object evaluate(Object domainObject, DataReader dataReader) {
        return null;
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return super.toString(sb.append("exists"));
    }

}