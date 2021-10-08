package bio.singa.structure.io.plip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class PiCation extends Interaction {

    /**
     * The Atom IDs of the source atoms of the interaction - belong to the protein.
     */
    private List<Integer> atoms1;

    /**
     * The Atom IDs of the target atoms of the interaction - belong to the ligand.
     */
    private List<Integer> atoms2;

    /**
     * The distance between the interacting atoms or groups in Angstrom.
     */
    private double distance;

    /**
     * The offset between the interacting groups.
     */
    private double offset;

    /**
     * Does the protein provide the charge?
     */
    private boolean protcharged;

    /**
     * The functional group in the ligand.
     */
    private String ligandGroup;

    public PiCation(int plipIdentifier) {
        super(plipIdentifier, InteractionType.PI_CATION_INTERACTION);
        atoms1 = new ArrayList<>();
        atoms2 = new ArrayList<>();
    }

    /**
     * Returns a list of the Atom IDs of the source atoms of the interaction, belonging to the protein.
     * @return The interaction's source atom IDs.
     */
    public List<Integer> getAtoms1() {
        return atoms1;
    }

    public void setAtoms1(List<Integer> atoms1) {
        this.atoms1 = atoms1;
    }

    /**
     * Returns a list of the Atom IDs of the target atoms of the interaction, belonging to the ligand.
     * @return The interaction's target atom IDs.
     */
    public List<Integer> getAtoms2() {
        return atoms2;
    }

    public void setAtoms2(List<Integer> atoms2) {
        this.atoms2 = atoms2;
    }

    /**
     * Returns the distance between the interacting atoms in Angstrom.
     * @return The distance between the interacting atoms.
     */
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the offset between the interacting groups.
     * @return The offset between the interacting groups.
     */
    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * Returns whether the protein provides the charge?
     * @return Whether the protein provides the charge.
     */
    public boolean isProtcharged() {
        return protcharged;
    }

    public void setProtcharged(boolean protcharged) {
        this.protcharged = protcharged;
    }

    /**
     * Returns the functional group in the ligand.
     * @return The ligand's functional group.
     */
    public String getLigandGroup() {
        return ligandGroup;
    }

    public void setLigandGroup(String ligandGroup) {
        this.ligandGroup = ligandGroup;
    }

    /**
     * Returns the Atom ID of the first atom in the list of source atoms, belonging to the protein.
     * @return The interaction's first source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        if (atoms1.iterator().hasNext()) {
            return atoms1.iterator().next();
        } else {
            return -1;
        }
    }

    /**
     * Returns the Atom ID of the first atom in the list of target atoms, belonging to the ligand.
     * @return The interaction's first target atom ID.
     */
    @Override
    public int getFirstTargetAtom() {
        if (atoms2.iterator().hasNext()) {
            return atoms2.iterator().next();
        } else {
            return -1;
        }
    }

    @Override
    public List<Integer> getAllSourceAtoms() {
        return atoms1;
    }

    @Override
    public List<Integer> getAllTargetAtoms() {
        return atoms2;
    }

    @Override
    public String toString() {
        return "PiCation{" +
                "atoms1=" + atoms1 +
                ", atoms2=" + atoms2 +
                ", plipIdentifier=" + plipIdentifier +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordinate=" + Arrays.toString(ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
