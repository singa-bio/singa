package de.bioforscher.chemistry.physical.branches;

/**
 * @author cl
 */
public class NucleicAcid extends BranchSubstructure<NucleicAcid> {

    public NucleicAcid(int identifier) {
        super(identifier);
    }

    public NucleicAcid(NucleicAcid nucleicAcid) {
        super(nucleicAcid);
    }

    @Override
    public NucleicAcid getCopy() {
        return new NucleicAcid(this);
    }
}
