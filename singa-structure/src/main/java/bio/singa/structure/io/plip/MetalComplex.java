package bio.singa.structure.io.plip;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class MetalComplex extends Interaction {

    /**
     * The Atom ID of the source atom of the interaction - this is the metal ion.
     */
    private int atom1;

    /**
     * The Atom ID of the target atom of the interaction - belongs to the corresponding interaction partner (either protein residue or ligand).
     */
    private int atom2;

    /**
     * The distance between the interacting atoms or groups in Angstrom.
     */
    private double distance;

    /**
     * The RMS of the geometry fit.
     */
    private double rms;

    /**
     * The metal coordination type.
     */
    private String geometry;

    /**
     * The location of the target group.
     */
    private String location;

    /**
     * The atom type of the metal.
     */
    private String metalType;

    /**
     * The metal coordination number.
     */
    private int coordination;

    /**
     * The continous numbering for the metal complex.
     */
    private int complexnum;

    public MetalComplex(int plipIdentifier) {
        super(plipIdentifier, InteractionType.METAL_COMPLEX);
    }

    /**
     * Returns the Atom ID of the source atom / metal ion.
     * @return The interaction's source atom ID.
     */
    public int getAtom1() {
        return atom1;
    }

    public void setAtom1(int atom1) {
        this.atom1 = atom1;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction, belonging to the interaction partner (protein residue or ligand).
     * @return The interaction's target atom ID.
     */
    public int getAtom2() {
        return atom2;
    }

    public void setAtom2(int atom2) {
        this.atom2 = atom2;
    }

    /**
     * Returns the distance between both interacting atoms in Angstrom.
     * @return The distance between the interacting atoms.
     */
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the RMS of the geometry fit.
     * @return The RMS of the geometry fit.
     */
    public double getRms() {
        return rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }

    /**
     * Returns the metal coordination type.
     * @return The metal coordination type.
     */
    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    /**
     * Returns the location of the target group.
     * @return The location of the target group.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the atom type of the metal.
     * @return The atom type of the metal.
     */
    public String getMetalType() {
        return metalType;
    }

    public void setMetalType(String metalType) {
        this.metalType = metalType;
    }

    /**
     * Returns the metal coordination number.
     * @return The metal ooordination number.
     */
    public int getCoordination() {
        return coordination;
    }

    public void setCoordination(int coordination) {
        this.coordination = coordination;
    }

    /**
     * Returns the continous numbering for the metal complex.
     * @return The continous numbering for the metal complex.
     */
    public int getComplexnum() {
        return complexnum;
    }

    public void setComplexnum(int complexnum) {
        this.complexnum = complexnum;
    }

    /**
     * Returns the Atom ID of the source atom. For {@link MetalComplex}es the first source atom is the atom ID of the metal ion.
     * This is the same as atom1.
     * @return The interaction's source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        return atom1;
    }

    /**
     * Returns the Atom ID of the target atom. For {@link MetalComplex}es the first target atom is the atom ID of the interaction partner (possibly
     * ligand or protein residue).
     * This is the same as atom2.
     * @return The interaction's target atom ID.
     */
    @Override
    public int getFirstTargetAtom() {
        return atom2;
    }

    @Override
    public List<Integer> getAllSourceAtoms() {
        return Collections.singletonList(atom1);
    }

    @Override
    public List<Integer> getAllTargetAtoms() {
        return Collections.singletonList(atom2);
    }

    @Override
    public String toString() {
        return "MetalComplex{" +
                "atom1=" + atom1 +
                ", atom2=" + atom2 +
                ", plipIdentifier=" + plipIdentifier +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordinate=" + Arrays.toString(ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
