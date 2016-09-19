package de.bioforscher.mathematics.functions;

import de.bioforscher.mathematics.functions.modifiers.ElementaryModifiers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Christoph on 09.08.2016.
 */
public class FunctionTests {

    @Test
    public void shouldSquareElementaryComponent() {
        Variable variable = new Variable("a", 3.0);
        ElementaryComponent component = new ElementaryComponent(ElementaryModifiers.square(), variable);
        assertEquals(9.0, component.getValue(), 0.0);
    }

    @Test
    public void shouldAddTermComponent() {
        Variable firstVariable = new Variable("a", 10.0);
        ElementaryComponent firstComponent = new ElementaryComponent(ElementaryModifiers.square(), firstVariable);
        Term firstTerm = new Term(firstComponent);

        Variable secondVariable = new Variable("b", 5.0);
        ElementaryComponent secondComponent = new ElementaryComponent(ElementaryModifiers.identity(), secondVariable);
        Term secondTerm = new Term(secondComponent);

        System.out.println(firstTerm.add(secondTerm));

        // assertEquals(15.0, firstTerm.add(secondTerm).getValue(), 0.0);


    }



}
