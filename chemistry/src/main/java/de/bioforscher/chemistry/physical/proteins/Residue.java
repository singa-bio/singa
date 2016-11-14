package de.bioforscher.chemistry.physical.proteins;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.bonds.Bond;
import de.bioforscher.chemistry.physical.model.Exchangeable;
import de.bioforscher.chemistry.physical.model.StructuralEntityType;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.core.utility.Nameable;

import java.util.EnumMap;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A residue is a grouping element that should only contain atoms. Each and every residue has a associate ResidueType,
 * that determines the amino acid (and the overarching features). Based on this ResidueType a Residue can be created
 * from a set of atoms that belong to this residue using the
 * {@link ResidueFactory#createResidueFromAtoms(int, ResidueType, EnumMap) ResidueFactory}. This establishes the bonds
 * in the amino acid, where possible.
 *
 * @author cl
 */
public class Residue extends SubStructure implements Nameable, Exchangeable<ResidueType> {

    /**
     * The type of this residue.
     */
    private final ResidueType type;

    /**
     * A iterating variable to add a new bond.
     */
    private int nextBondIdentifier;

    /**
     * Creates a new Residue with a identifier and ResidueType. Preferably the
     * {@link ResidueFactory#createResidueFromAtoms(int, ResidueType, EnumMap) ResidueFactory} should be used to create
     * Residues.
     *
     * @param identifier The identifier.
     * @param type       The ResidueType.
     */
    public Residue(int identifier, ResidueType type) {
        super(identifier);
        this.type = type;
        this.nextBondIdentifier = 0;
    }

    /**
     * Connects two atoms with a bond, returning whether the connection could be made.
     *
     * @param first  The first atom.
     * @param second The second atom.
     * @return True if the connection could be assigned.
     */
    public boolean connect(Atom first, Atom second) {
        if (first == null || second == null) {
            return false;
        }
        // create bond
        Bond bond = new Bond();
        bond.setIdentifier(this.nextBondIdentifier);
        bond.setSource(first);
        bond.setTarget(second);
        // add edges
        this.addEdge(this.nextBondIdentifier, bond);
        first.addNeighbour(second);
        second.addNeighbour(first);
        // increase identifier
        this.nextBondIdentifier++;
        return true;
    }

    /**
     * Return the name of this residue in the format [Three Letter Code of the Residue]:[Residue identifier] (e.g.
     * Arg:123 or Met:17).
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return this.type.getName() + ":" + getIdentifier();
    }

    /**
     * Returns the one letter code of this residue.
     *
     * @return The one letter code of this residue.
     */
    public String getOneLetterCode() {
        return this.type.getOneLetterCode();
    }

    /**
     * Returns the three letter code of this residue.
     *
     * @return The three letter code of this residue.
     */
    public String getThreeLetterCode() {
        return this.type.getThreeLetterCode();
    }

    /**
     * Gets the atom with this name, if possible.
     *
     * @param atomName The name of the atom.
     * @return The Atom associated to this name.
     * @throws NoSuchElementException if there is no atom with this name.
     */
    public Atom getAtomByName(AtomName atomName) {
        return getNodes().stream()
                .filter(atom -> atom.getAtomName() == atomName)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the {@link AtomName#CA alpha carbon} (carbon with the side cain attached).
     *
     * @return The C alpha carbon.
     */
    public Atom getCAlpha() {
        return getAtomByName(AtomName.CA);
    }

    /**
     * Returns the {@link AtomName#CB beta carbon} (first carbon of the side cain).
     *
     * @return The C beta carbon.
     */
    public Atom getCBeta() {
        return getAtomByName(AtomName.CB);
    }

    /**
     * Returns the {@link AtomName#C backbone carbon} (carbon in the backbone).
     *
     * @return The backbone carbon.
     */
    public Atom getBackboneCarbon() {
        return getAtomByName(AtomName.C);
    }

    /**
     * Returns the {@link AtomName#N backbone nitrogen} (nitrogen in the backbone).
     *
     * @return The backbone nitrogen.
     */
    public Atom getBackboneNitrogen() {
        return getAtomByName(AtomName.N);
    }

    /**
     * Returns the {@link AtomName#O backbone oxygen} (oxygen in the backbone).
     *
     * @return The backbone oxygen.
     */
    public Atom getBackboneOxygen() {
        return getAtomByName(AtomName.O);
    }

    /**
     * Return the name of this residue in the format [Three Letter Code of the Residue]:[Residue identifier] (e.g.
     * Arg:123 or Met:17).
     *
     * @return The String representation of this residue.
     */
    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public ResidueType getType() {
        return type;
    }
}
