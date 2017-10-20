package de.bioforscher.singa.mmtf;

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
     * The original bytes kept to copy.
     */
    private byte[] bytes;

    /**
     * The original mmtf data.
     */
    private StructureDataInterface data;

    /**
     * The index of the parent leaf in the group data arrays.
     */
    private int internalGroupIndex;

    /**
     * Index of this atom in the associated group (e.g. N = 0, CA = 1, ...)
     */
    private int groupPositionIndex;

    /**
     * Index in atom data arrays (e.g. coordinates).
     */
    private int internalAtomIndex;

    /**
     * Creates a new {@link MmtfAtom}.
     *
     * @param data The original mmtf data.
     * @param internalGroupIndex Index of associated group in data array.
     * @param groupPositionIndex Index of this atom in the associated group.
     * @param internalAtomIndex Index in atom data arrays.
     */
    MmtfAtom(StructureDataInterface data, byte[] bytes, int internalGroupIndex, int groupPositionIndex, int internalAtomIndex) {
        this.bytes = bytes;
        this.data = MmtfStructure.bytesToStructureData(bytes);
        this.internalGroupIndex = internalGroupIndex;
        this.internalAtomIndex = internalAtomIndex;
        this.groupPositionIndex = groupPositionIndex;
    }

    @Override
    public Integer getIdentifier() {
        return internalAtomIndex + 1;
    }

    @Override
    public String getAtomName() {
        // get relevant string for this group type
        return data.getGroupAtomNames(data.getGroupTypeIndices()[internalGroupIndex])[groupPositionIndex];
    }

    @Override
    public Vector3D getPosition() {
        // assemble position from internal atom identifier
        return new Vector3D(data.getxCoords()[internalAtomIndex], data.getyCoords()[internalAtomIndex], data.getzCoords()[internalAtomIndex]);
    }

    @Override
    public void setPosition(Vector3D position) {
        data.getxCoords()[internalAtomIndex] = (float) position.getX();
        data.getyCoords()[internalAtomIndex] = (float) position.getY();
        data.getzCoords()[internalAtomIndex] = (float) position.getZ();
    }

    @Override
    public Element getElement() {
        return ElementProvider.getElementBySymbol(data.getGroupElementNames(data.getGroupTypeIndices()[internalGroupIndex])[groupPositionIndex])
                .orElse(ElementProvider.UNKOWN);
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
