package bio.singa.simulation.model.sections;

/**
 * The cell topology defines how the subsections are spatially organized. A subsection is considered inner if a membrane
 * is enclosing the subsection, isolating it from outer influences. A subsection is considered membrane, when it
 * separates inside and outside of a cell section.
 * For example the nucleus region of the cell could have the following topologies: INNER: nucleoplasm, MEMBRANE: nuclear
 * envelope and OUTER: Cytoplasm.
 *
 * @author cl
 */
public enum CellTopology {

    /**
     * A membrane is enclosing this subsection.
     */
    INNER(0),

    /**
     * The membrane separating inner and outer subsections.
     */
    MEMBRANE(1),

    /**
     * A membrane is separating this subsection form the inner subsection.
     */
    OUTER(2);

    private int index;

    CellTopology(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static CellTopology getTopology(int index) {
        if (index == 0) {
            return INNER;
        }
        if (index == 1) {
            return MEMBRANE;
        }
        return OUTER;
    }

}
