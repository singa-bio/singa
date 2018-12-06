package bio.singa.structure.parser.plip;


import bio.singa.structure.model.identifiers.LeafIdentifier;

/**
 * @author cl
 */
public abstract class Interaction {

    /**
     * The PLIP-identifier of the interaction.
     */
    int plipIdentifier;

    /**
     * The LeafIdentifier of the interaction source.
     */
    LeafIdentifier source;

    /**
     * The LeafIdentifier of the interaction target.
     */
    LeafIdentifier target;

    /**
     * The coordinates of the interacting ligand atom or interaction center in ligand.
     */
    double[] ligandCoordinate;

    /**
     * The coordinates of the interacting protein atom or interaction center in protein.
     */
    double[] proteinCoordinate;

    InteractionType interactionType;

    public Interaction(int plipIdentifier, InteractionType interactionType) {
        this.plipIdentifier = plipIdentifier;
        this.interactionType = interactionType;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    /**
     * Returns the LeafIdentifier of the interaction source.
     *
     * @return The interaction source.
     */
    public LeafIdentifier getSource() {
        return source;
    }

    public void setSource(LeafIdentifier source) {
        this.source = source;
    }

    /**
     * Returns the LeafIdentifier of the interaction target.
     *
     * @return The interaction target.
     */
    public LeafIdentifier getTarget() {
        return target;
    }

    public void setTarget(LeafIdentifier target) {
        this.target = target;
    }

    /**
     * Returns the coordinates of the interacting ligand atom or interaction center in ligand.
     *
     * @return The coordinates of the interacting ligand atom.
     */
    public double[] getLigandCoordinate() {
        return ligandCoordinate;
    }

    public void setLigandCoordinate(double[] ligandCoordinate) {
        this.ligandCoordinate = ligandCoordinate;
    }

    /**
     * Returns the coordinates of the interacting protein atom or interaction center in protein.
     *
     * @return The coordinates of the interacting protein atom.
     */
    public double[] getProteinCoordinate() {
        return proteinCoordinate;
    }

    public void setProteinCoordinate(double[] proteinCoordinate) {
        this.proteinCoordinate = proteinCoordinate;
    }

    /**
     * Returns the PLIP-identifier of the interaction.
     *
     * @return The PLIP-identifier of the interaction.
     */
    public int getPlipIdentifier() {
        return plipIdentifier;
    }

    public void setPlipIdentifier(int plipIdentifier) {
        this.plipIdentifier = plipIdentifier;
    }

    /**
     * Returns the Atom ID of the source atom of the interaction, belonging to the protein. Pay attention to interaction
     * type in question since definition may vary accordingly.
     *
     * @return The interaction's source atom ID.
     */
    abstract public int getFirstSourceAtom();

    /**
     * Returns the Atom ID of the target atom of the interaction, belonging to the ligand. Pay attention to interaction
     * type in question since definition may vary accordingly.
     *
     * @return The interaction's target atom ID.
     */
    abstract public int getFirstTargetAtom();

}
