package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.model.StructuralFamily;

/**
 * Created by fkaiser on 06.12.16.
 */
public class AtomContainer<StructuralFamilyType extends StructuralFamily>
        extends LeafSubstructure<AtomContainer<StructuralFamilyType>, StructuralFamilyType> {

    private StructuralFamilyType family;

    public AtomContainer(int identifier, StructuralFamilyType family) {
        super(identifier);
        this.family = family;
    }

    public AtomContainer(AtomContainer atomContainer) {
        super(atomContainer);
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
}
