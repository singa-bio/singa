package de.bioforscher.singa.chemistry.physical.leaves;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.StructuralFamily;

/**
 * The AtomContainer is used for LeafSubstructures the are not assignable to either {@link AminoAcid} or {@link
 * Nucleotide}. This container is used for ligands or to group atoms otherwise.
 *
 * @author fk
 */
public class AtomContainer<StructuralFamilyType extends StructuralFamily>
        extends LeafSubstructure<AtomContainer<StructuralFamilyType>, StructuralFamilyType> {

    /**
     * The name of this container.
     */
    private String name;

    /**
     * Creates a new AtomContainer with an identifier and {@link StructuralFamily}. The name is set to the three letter
     * code of the family.
     *
     * @param leafIdentifier The identifier.
     * @param family The family.
     */
    public AtomContainer(LeafIdentifier leafIdentifier, StructuralFamilyType family) {
        super(leafIdentifier, family);
        this.name = family.getThreeLetterCode();
    }

    /**
     * Creates a new AtomContainer with an identifier and {@link StructuralFamily}.
     *
     * @param leafIdentifier The identifier.
     * @param family The family.
     * @param name The name of this container.
     */
    public AtomContainer(LeafIdentifier leafIdentifier, StructuralFamilyType family, String name) {
        super(leafIdentifier, family);
        this.name = name;
    }

    /**
     * This is a copy constructor. Creates a new atom container with the same attributes as the given atom container.
     * This also recursively creates copies of all the underlying substructures and atoms. The neighbours of this
     * substructure are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations
     * to the lifecycle of the substructure to copy. If you want to keep the neighbouring substructures, copy the
     * superordinate substructure that contains this substructure and it will also traverse and copy the neighbouring
     * substructures.
     *
     * @param atomContainer The atom container to copy
     */
    public AtomContainer(AtomContainer<StructuralFamilyType> atomContainer) {
        super(atomContainer);
        this.name = atomContainer.name;
    }

    @Override
    public AtomContainer<StructuralFamilyType> getCopy() {
        return new AtomContainer<>(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this container.
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

}
