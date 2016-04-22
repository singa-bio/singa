package de.bioforscher.simulation.reactions;

/**
 * This class can be used to annotate a reaction. Currently not in use.
 *
 * @author Christoph Leberecht
 */
public class ReactionAnnotation {

    private String tissue;
    private String organism;
    private String pathway;

    public ReactionAnnotation() {
        super();
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getPathway() {
        return pathway;
    }

    public void setPathway(String pathway) {
        this.pathway = pathway;
    }

}
