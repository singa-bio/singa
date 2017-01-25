package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author fk
 */
public class AtomContainer<StructuralFamilyType extends StructuralFamily>
        extends LeafSubstructure<AtomContainer<StructuralFamilyType>, StructuralFamilyType> {

    private String name;

    public AtomContainer(LeafIdentifier leafIdentifier, StructuralFamilyType family) {
        super(leafIdentifier, family);
        this.name = family.getThreeLetterCode();
    }

    public AtomContainer(LeafIdentifier leafIdentifier, StructuralFamilyType family, String name) {
        super(leafIdentifier, family);
        this.name = name;
    }

    public AtomContainer(AtomContainer atomContainer) {
        super(atomContainer);
        this.name = atomContainer.name;
    }

    @Override
    public AtomContainer<StructuralFamilyType> getCopy() {
        return new AtomContainer<>(this);
    }

    @Override
    public String toString() {
        return this.name + ":" + getIdentifier();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new NotImplementedException();
    }
}
