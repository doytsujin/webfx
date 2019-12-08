package webfx.framework.shared.orm.expression.terms;

import webfx.framework.shared.orm.expression.Expression;
import webfx.framework.shared.orm.expression.lci.DomainReader;

import java.util.Collection;

/**
 * @author Bruno Salmon
 */
public final class As<T> extends UnaryExpression<T> {

    private final String alias;

    public As(Expression<T> operand, String alias) {
        super(operand);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Object evaluate(T domainObject, DomainReader<T> domainReader) {
        // Reading the alias field which is supposed to be stored in the domain object
        return domainReader.getDomainFieldValue(domainObject, alias);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return operand.toString(sb).append(" as ").append(alias);
    }

    public void collectPersistentTerms(Collection<Expression<T>> persistentTerms) {
        persistentTerms.add(this); // We want the whole As expression to be sent to the server
    }
}
