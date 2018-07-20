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
    INNER,

    /**
     * A membrane is separating this subsection form the inner subsection.
     */
    OUTER,

    /**
     * The membrane separating inner and outer subsections.
     */
    MEMBRANE,

}
