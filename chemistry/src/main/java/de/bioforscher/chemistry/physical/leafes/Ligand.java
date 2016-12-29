package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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


    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new NotImplementedException();
    }
}
