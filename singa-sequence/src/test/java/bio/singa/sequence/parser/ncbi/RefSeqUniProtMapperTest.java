package bio.singa.sequence.parser.ncbi;

import bio.singa.features.identifiers.RefSeqIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefSeqUniProtMapperTest {

    @Test
    @Disabled
    void map() {
        List<UniProtIdentifier> uniProtIdentifiers = new RefSeqUniProtMapper(new RefSeqIdentifier("NM_199242.2")).parse();
        assertEquals(1, uniProtIdentifiers.size());
        uniProtIdentifiers = new RefSeqUniProtMapper(new RefSeqIdentifier("NM_001242524.1")).parse();
        assertEquals(2, uniProtIdentifiers.size());
    }

}