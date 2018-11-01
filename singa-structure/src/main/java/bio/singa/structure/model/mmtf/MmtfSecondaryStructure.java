package bio.singa.structure.model.mmtf;

/**
 * Constants representing the type of secondary structure as specified by the MMTF standard and annotated by DSSP.
 *
 * @author fk
 * @see <a href="https://github.com/rcsb/mmtf/blob/v1.0/spec.md#structure-data">MMTF specification</a>
 * @see <a href="https://onlinelibrary.wiley.com/doi/epdf/10.1002/bip.360221211">DSSP</a>
 */
public enum MmtfSecondaryStructure {

    PI_HELIX(0),
    BEND(1),
    ALPHA_HELIX(2),
    EXTENDED(3),
    THREE_TEN_HELIX(4),
    BRIDGE(5),
    TURN(6),
    COIL(7),
    UNDEFINED(-1);

    private int mmtfCode;

    MmtfSecondaryStructure(int mmtfCode) {

        this.mmtfCode = mmtfCode;
    }

    public static MmtfSecondaryStructure getByMmtfCode(int mmtfCode) {
        if (mmtfCode == -1) {
            return UNDEFINED;
        }
        return MmtfSecondaryStructure.values()[mmtfCode];
    }

    public int getMmtfCode() {
        return mmtfCode;
    }
}
