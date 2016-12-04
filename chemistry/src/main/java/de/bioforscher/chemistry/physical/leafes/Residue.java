package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.families.ResidueFactory;
import de.bioforscher.chemistry.physical.families.ResidueFamily;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A residue is a grouping element that should only contain atoms. Each and every residue has a associate ResidueType,
 * that determines the amino acid (and the overarching features). Based on this ResidueType a Residue can be created
 * from a set of atoms that belong to this residue using the
 * {@link ResidueFactory#createResidueFromAtoms(int, ResidueFamily, EnumMap) ResidueFactory}. This establishes the bonds
 * in the amino acid, where possible.
 *
 * @author cl
 */
public class Residue extends LeafSubstructure<Residue, ResidueFamily> {

    /**
     * The family of this residue.
     */
    private final ResidueFamily family;

    /**
     * A set of exchangeable types that are considered to be identical.
     */
    private Set<ResidueFamily> exchangeableTypes;

    /**
     * Creates a new Residue with a identifier and ResidueType. Preferably the
     * {@link ResidueFactory#createResidueFromAtoms(int, ResidueFamily, EnumMap) ResidueFactory} should be used to create
     * Residues.
     *
     * @param identifier The identifier.
     * @param family       The ResidueType.
     */
    public Residue(int identifier, ResidueFamily family) {
        super(identifier);
        this.family = family;
        this.exchangeableTypes = new HashSet<>();
    }

    /**
     * This is a copy constructor. Creates a new residue with the same attributes as the given residue. This
     * also recursively creates copies of all the underlying substructures and atoms. The neighbours of this
     * substructure are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations
     * to the lifecycle of the substructure to copy. If you want to keep the neighbouring substructures, copy the
     * superordinate substructure that contains this substructure and it will also traverse and copy the neighbouring
     * substructures.
     *
     * @param residue The residue to copy
     */
    public Residue(Residue residue) {
        super(residue);
        this.family = residue.family;
    }

    @Override
    public Set<ResidueFamily> getExchangeableTypes() {
        return this.exchangeableTypes;
    }

    /**
     * Returns the one letter code of this residue.
     *
     * @return The one letter code of this residue.
     */
    public String getOneLetterCode() {
        return this.family.getOneLetterCode();
    }

    /**
     * Returns the three letter code of this residue.
     *
     * @return The three letter code of this residue.
     */
    public String getThreeLetterCode() {
        return this.family.getThreeLetterCode();
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
        return this.family.getName() + ":" + getIdentifier();
    }

    public ResidueFamily getFamily() {
        return this.family;
    }

    @Override
    public Residue getCopy() {
        return new Residue(this);
    }


}
