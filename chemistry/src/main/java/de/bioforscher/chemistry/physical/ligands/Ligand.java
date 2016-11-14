package de.bioforscher.chemistry.physical.ligands;

import de.bioforscher.chemistry.physical.model.SubStructure;
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
public class Ligand extends SubStructure implements Nameable {
    /**
     * Creates a new SubStructure. The identifier is considered in the superordinate SubStructure.
     *
     * @param identifier The identifier of this SubStructure.
     */
    public Ligand(int identifier) {
        super(identifier);
    }

    @Override
    public String getName() {
        return null;
    }
}
