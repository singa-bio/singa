package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link Atom} for mmtf structures. Remembers the internal group index (parent reference), the
 * group position index and the index for the atom data array.
 *
 * @author cl
 */
public class MmtfAtom implements Atom {

    /**
     * The original mmtf data.
     */
    private final StructureDataInterface data;

    /**
     * The index of the parent leaf in the group data arrays.
     */
    private final int internalGroupIndex;

    /**
     * Index of this atom in the associated group (e.g. N = 0, CA = 1, ...)
     */
    private final int groupPositionIndex;

    /**
     * Index in atom data arrays (e.g. coordinates).
     */
    private final int internalAtomIndex;

    /**
     * The cached position of the atom.
     */
    private Vector3D cachedPosition;

    /**
     * Creates a new {@link MmtfAtom}.
     *
     * @param data The original mmtf data.
     * @param internalGroupIndex Index of associated group in data array.
     * @param groupPositionIndex Index of this atom in the associated group.
     * @param internalAtomIndex Index in atom data arrays.
     */
    MmtfAtom(StructureDataInterface data, int internalGroupIndex, int groupPositionIndex, int internalAtomIndex) {
        this.data = data;
        this.internalGroupIndex = internalGroupIndex;
        this.internalAtomIndex = internalAtomIndex;
        this.groupPositionIndex = groupPositionIndex;
    }

    public MmtfAtom(MmtfAtom mmtfAtom) {
        data = mmtfAtom.data;
        internalGroupIndex = mmtfAtom.internalGroupIndex;
        internalAtomIndex = mmtfAtom.internalAtomIndex;
        groupPositionIndex = mmtfAtom.groupPositionIndex;
        cachedPosition = mmtfAtom.cachedPosition;
    }

    @Override
    public Integer getAtomIdentifier() {
        return internalAtomIndex + 1;
    }

    @Override
    public String getAtomName() {
        // get relevant string for this group type
        return data.getGroupAtomNames(data.getGroupTypeIndices()[internalGroupIndex])[groupPositionIndex];
    }

    @Override
    public Vector3D getPosition() {
        if (cachedPosition == null) {
            // assemble position from internal atom identifier
            cachedPosition = new Vector3D(data.getxCoords()[internalAtomIndex], data.getyCoords()[internalAtomIndex], data.getzCoords()[internalAtomIndex]);
        }
        return cachedPosition;

    }

    @Override
    public void setPosition(Vector3D position) {
        cachedPosition = position;
    }

    @Override
    public Element getElement() {
        return ElementProvider.getElementBySymbol(data.getGroupElementNames(data.getGroupTypeIndices()[internalGroupIndex])[groupPositionIndex])
                .orElse(ElementProvider.UNKOWN);
    }

    @Override
    public Atom getCopy() {
        return new MmtfAtom(this);
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
