package uk.co.cogitolearning.cogpar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class CogParTest {

    @Test
    public void shoudparsAndEvaulateExpression() {
        String exprstr = "2*(1+sin(pi/2))^2";
        ExpressionParser parser = new ExpressionParser();
        try {
            ExpressionNode expr = parser.parse(exprstr);
            expr.accept(new SetVariable("pi", Math.PI));
            assertEquals(8.0, expr.getValue(), 0.0);
        } catch (ParserException | EvaluationException e) {
            e.printStackTrace();
        }
    }

}