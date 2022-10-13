package bio.singa.structure.model.mmtf;

import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;
import org.apache.commons.lang.NotImplementedException;
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
     * The cached atom name, will not be persisted.
     */
    private String cachedAtomName;

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
        cachedAtomName = mmtfAtom.cachedAtomName;
    }

    @Override
    public int getAtomIdentifier() {
        return internalAtomIndex + 1;
    }

    @Override
    public String getAtomName() {
        // get relevant string for this group type
        if (cachedAtomName != null) {
            return cachedAtomName;
        }
        return data.getGroupAtomNames(data.getGroupTypeIndices()[internalGroupIndex])[groupPositionIndex];
    }

    @Override
    public void setAtomName(String atomName) {
        cachedAtomName = atomName;
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
    public double getBFactor() {
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public void setBFactor(double bFactor) {
        throw new NotImplementedException("Not yet implemented");
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
