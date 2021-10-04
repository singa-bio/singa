package bio.singa.structure.model.pdb;

import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;

/**
 * @author cl
 */
public class PdbLinkEntry {

    private LeafSubstructure firstLeafSubstructure;
    private Atom firstAtom;
    private LeafSubstructure secondLeafSubstructure;
    private Atom secondAtom;

    public PdbLinkEntry(LeafSubstructure firstLeafSubstructure, Atom firstAtom, LeafSubstructure secondLeafSubstructure, Atom secondAtom) {
        this.firstLeafSubstructure = firstLeafSubstructure;
        this.firstAtom = firstAtom;
        this.secondLeafSubstructure = secondLeafSubstructure;
        this.secondAtom = secondAtom;
    }

    public LeafSubstructure getFirstLeafSubstructure() {
        return firstLeafSubstructure;
    }

    public void setFirstLeafSubstructure(LeafSubstructure firstLeafSubstructure) {
        this.firstLeafSubstructure = firstLeafSubstructure;
    }

    public Atom getFirstAtom() {
        return firstAtom;
    }

    public void setFirstAtom(Atom firstAtom) {
        this.firstAtom = firstAtom;
    }

    public LeafSubstructure getSecondLeafSubstructure() {
        return secondLeafSubstructure;
    }

    public void setSecondLeafSubstructure(LeafSubstructure secondLeafSubstructure) {
        this.secondLeafSubstructure = secondLeafSubstructure;
    }

    public Atom getSecondAtom() {
        return secondAtom;
    }

    public void setSecondAtom(Atom secondAtom) {
        this.secondAtom = secondAtom;
    }
}
