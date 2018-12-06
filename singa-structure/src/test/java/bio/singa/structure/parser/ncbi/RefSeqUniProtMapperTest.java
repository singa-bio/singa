package bio.singa.structure.parser.ncbi;

import bio.singa.features.identifiers.RefSeqIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefSeqUniProtMapperTest {

    @Test
    void map() {
        Map<UniProtIdentifier, String> uniProtIdentifiers = new RefSeqUniProtMapper(new RefSeqIdentifier("NM_199242.2")).parse();
        assertEquals(1, uniProtIdentifiers.size());
        uniProtIdentifiers = new RefSeqUniProtMapper(new RefSeqIdentifier("NM_001242524.1")).parse();
        assertEquals(2, uniProtIdentifiers.size());
    }
}