package uk.co.cogitolearning.cogpar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class CogParTest {

    @Test
    void shoudparsAndEvaulateExpression() {
        String exprstr = "2*(1+sin(pi/2))^2";
        ExpressionParser parser = new ExpressionParser();
        try {
            ExpressionNode expr = parser.parse(exprstr);
            expr.accept(new SetVariable("pi", Math.PI));
            assertEquals(8.0, expr.getValue());
        } catch (ParserException | EvaluationException e) {
            e.printStackTrace();
        }
    }

}