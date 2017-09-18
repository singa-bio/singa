package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * @author cl
 */
public class MmtfAtom implements AtomInterface {

    private StructureDataInterface data;

    /**
     * Position of this atom in the associated group (e.g. N = 0, CA = 1, ...)
     */
    private int groupPositionIndex;

    /**
     * Position of associated group in data array.
     */
    private int internalGroupIndex;

    /**
     * Position in data array (e.g. coordinates)
     */
    private int internalAtomIndex;

    public MmtfAtom(StructureDataInterface data, int internalGroupIndex, int groupPositionIndex, int internalAtomIndex) {
        this.data = data;
        this.internalGroupIndex = internalGroupIndex;
        this.internalAtomIndex = internalAtomIndex;
        this.groupPositionIndex = groupPositionIndex;
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
}
