package bio.singa.structure.parser.pdb.rest.cluster;

import bio.singa.structure.model.identifiers.PDBIdentifier;

/**
 * @author fk
 */
public class PDBSequenceClusterMember {

    private int rank;
    private PDBIdentifier pdbIdentifier;
    private String chainIdentifier;

    PDBSequenceClusterMember(int rank, PDBIdentifier pdbIdentifier, String chainIdentifier) {
        this.rank = rank;
        this.pdbIdentifier = pdbIdentifier;
        this.chainIdentifier = chainIdentifier;
    }

    public int getRank() {
        return rank;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public PDBIdentifier getPdbIdentifier() {

        return pdbIdentifier;
    }
}
