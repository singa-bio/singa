package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author fk
 */
public class AtomContainer<StructuralFamilyType extends StructuralFamily>
        extends LeafSubstructure<AtomContainer<StructuralFamilyType>, StructuralFamilyType> {

    private StructuralFamilyType family;

    private String name;

    public AtomContainer(int identifier, StructuralFamilyType family) {
        super(identifier);
        this.family = family;
    }

    public AtomContainer(int identifier, StructuralFamilyType family, String name) {
        super(identifier);
        this.family = family;
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
        return this.family.getThreeLetterCode() + ":" + getIdentifier();
    }


    @Override
    public StructuralFamilyType getFamily() {
        return this.family;
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
