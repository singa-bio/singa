package bio.singa.structure.parser.pdb.rest.cluster;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class PDBSequenceClusterTest {

    @Test
    void shouldObtainSequenceCluster() {
        PDBSequenceCluster sequenceCluster = PDBSequenceCluster.of("1zuh", "A",
                PDBSequenceCluster.PDBSequenceClusterIdentity.IDENTITY_95);
        assertEquals("1zuh", sequenceCluster.getClusterMembers().get(0).getPdbIdentifier().getContent());
        assertEquals("A", sequenceCluster.getClusterMembers().get(0).getChainIdentifier());
        assertEquals("3hr7", sequenceCluster.getClusterMembers().get(1).getPdbIdentifier().getContent());
        assertEquals("A", sequenceCluster.getClusterMembers().get(1).getChainIdentifier());
    }

    @Test
    void shouldDiscardHomomericStructures() {
        PDBSequenceCluster sequenceCluster1 = PDBSequenceCluster.of("1f6d", "A",
                PDBSequenceCluster.PDBSequenceClusterIdentity.IDENTITY_95);
        assertEquals("1f6d", sequenceCluster1.getRepresentative()[0]);
        assertEquals("A", sequenceCluster1.getRepresentative()[1]);

        PDBSequenceCluster sequenceCluster2 = PDBSequenceCluster.of("1f6d", "B",
                PDBSequenceCluster.PDBSequenceClusterIdentity.IDENTITY_95);
        assertEquals("1f6d", sequenceCluster2.getRepresentative()[0]);
        assertEquals("A", sequenceCluster2.getRepresentative()[1]);
    }
}