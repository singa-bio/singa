package bio.singa.features.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RefSeqIdentifierTest {

    @Test
    void matchIdentifierPattern() {
        RefSeqIdentifier refSeqIdentifier = new RefSeqIdentifier("NM_001130528.2");
        assertNotNull(refSeqIdentifier);
    }
}