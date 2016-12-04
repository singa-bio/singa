package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.core.utility.Nameable;

/**
 * A ligand is a grouping element that should only contain
 * {@link de.bioforscher.chemistry.physical.atoms.UncertainAtom}s because there are endless possibilities for
 * ligand atom nomenclature.
 *
 * TODO: this is a stub and not yet implemented
 *
 * @author fk
 */
public class Ligand extends BranchSubstructure implements Nameable {
    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public Ligand(int identifier) {
        super(identifier);
    }

    @Override
    public BranchSubstructure getCopy() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
