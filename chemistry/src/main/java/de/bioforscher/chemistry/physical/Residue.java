package de.bioforscher.chemistry.physical;

import de.bioforscher.core.utility.Nameable;

import java.util.Optional;

/**
 * Created by Christoph on 22.09.2016.
 */
public class Residue extends SubStructure implements Nameable {

    private final ResidueType type;
    private int nextBondIdentifier;

    public Residue(int identifier, ResidueType type) {
        super(identifier);
        this.type = type;
        this.nextBondIdentifier = 0;
    }

    public void connect(Atom first, Atom second) {
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
    }


    @Override
    public String getName() {
        return this.type.getName();
    }

    public String getOneLetterCode() {
        return this.type.getOneLetterCode();
    }

    public String getThreeLetterCode() {
        return this.type.getThreeLetterCode();
    }

    public Optional<Atom> getAtomByName(AtomName atomName) {
        return getNodes().stream().filter(atom -> atom.getAtomName() == atomName).findFirst();
    }

    public Optional<Atom> getCAlpha() {
        return getAtomByName(AtomName.CA);
    }

    public Optional<Atom> getCBeta() {
        return getAtomByName(AtomName.CB);
    }

    public Optional<Atom> getBackboneCarbon() {
        return getAtomByName(AtomName.C);
    }

    public Optional<Atom> getBackboneNitrogen() {
        return getAtomByName(AtomName.N);
    }

    public Optional<Atom> getBackboneOxygen() {
        return getAtomByName(AtomName.O);
    }

    @Override
    public String toString() {
        return "Residue{" +
                this.identifier + ":" +
                this.getName() +
                '}';
    }
}
