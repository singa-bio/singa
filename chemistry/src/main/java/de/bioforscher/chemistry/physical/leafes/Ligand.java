package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.core.utility.Nameable;

/**
 * A ligand is a grouping element that should only contain
 * {@link de.bioforscher.chemistry.physical.atoms.UncertainAtom}s because there are endless possibilities for
 * ligand atom nomenclature.
 *
 * @author fk
 */
public class Ligand extends LeafSubstructure<Ligand, StructuralFamily> {


    public Ligand(int identifier) {
        super(identifier);
    }

    public Ligand(LeafSubstructure<?, ?> leafSubstructure) {
        super(leafSubstructure);
    }

    @Override
    public Ligand getCopy() {
        return null;
    }

    @Override
    public StructuralFamily getFamily() {
        return null;
    }
}
