package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.Identifier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ECNumberTest {

	/*
     * Valid EC Numbers
	 */

    @Test
    public void testECNumber01() {
        Identifier ecNumber = new ECNumber("1");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber03() {
        Identifier ecNumber = new ECNumber("1.2");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber04() {
        Identifier ecNumber = new ECNumber("1.23");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber06() {
        Identifier ecNumber = new ECNumber("1.23.4");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber07() {
        Identifier ecNumber = new ECNumber("1.23.45");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber09() {
        Identifier ecNumber = new ECNumber("1.23.45.6");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber10() {
        Identifier ecNumber = new ECNumber("1.23.45.67");
        assertNotNull(ecNumber);
    }

    @Test
    public void testECNumber11() {
        Identifier ecNumber = new ECNumber("1.23.45.678");
        assertNotNull(ecNumber);
    }
	
	/*
	 * Invalid EC Numbers
	 */

    @Test(expected = IllegalArgumentException.class)
    public void testECNumber02() {
        Identifier ecNumber = new ECNumber("11");
        assertNotNull(ecNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testECNumber05() {
        Identifier ecNumber = new ECNumber("1.234");
        assertNotNull(ecNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testECNumber08() {
        Identifier ecNumber = new ECNumber("1.23.456");
        assertNotNull(ecNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testECNumber12() throws IllegalArgumentException {
        Identifier ecNumber = new ECNumber("1.23.45.6789");
        assertNotNull(ecNumber);
    }

}
