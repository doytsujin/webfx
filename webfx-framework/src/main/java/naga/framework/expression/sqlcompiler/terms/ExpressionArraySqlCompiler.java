package naga.framework.expression.sqlcompiler.terms;

import naga.framework.expression.Expression;
import naga.framework.expression.terms.ExpressionArray;

/**
 * @author Bruno Salmon
 */
public class ExpressionArraySqlCompiler extends AbstractTermSqlCompiler<ExpressionArray> {

    public ExpressionArraySqlCompiler() {
        super(ExpressionArray.class);
    }

    @Override
    public void compileExpressionToSql(ExpressionArray e, Options o) {
        for (Expression child : e.getExpressions())
            compileChildExpressionToSql(child, o);
    }
}