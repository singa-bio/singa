package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ECNumberTest {

    /*
     * Valid EC Numbers
     */

    @Test
    void testECNumber01() {
        Identifier ecNumber = new ECNumber("1");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber03() {
        Identifier ecNumber = new ECNumber("1.2");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber04() {
        Identifier ecNumber = new ECNumber("1.23");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber06() {
        Identifier ecNumber = new ECNumber("1.23.4.-");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber07() {
        Identifier ecNumber = new ECNumber("1.23.45.-");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber09() {
        Identifier ecNumber = new ECNumber("1.23.45.6");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber10() {
        Identifier ecNumber = new ECNumber("1.23.45.67");
        assertNotNull(ecNumber);
    }

    @Test
    void testECNumber11() {
        Identifier ecNumber = new ECNumber("1.23.45.678");
        assertNotNull(ecNumber);
    }

    /*
     * Invalid EC Numbers
     */

    @Test
    void testECNumber02() {
        assertThrows(IllegalArgumentException.class,
                () -> new ECNumber("11"));
    }

    @Test
    void testECNumber05() {
        assertThrows(IllegalArgumentException.class,
                () -> new ECNumber("1.234"));
    }

    @Test
    void testECNumber08() {
        assertThrows(IllegalArgumentException.class,
                () -> new ECNumber("1.23.456"));

    }

    @Test
    void testECNumber12() {
        assertThrows(IllegalArgumentException.class,
                () -> new ECNumber("1.23.45.6789"));
    }

}
