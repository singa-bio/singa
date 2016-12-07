package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static de.bioforscher.chemistry.parser.pdb.PDBParsingTreeNode.StructureLevel.*;

/**
 * Created by leberech on 07/12/16.
 */
public class PDBParsingTreeNode {

    public enum StructureLevel {
        STRUCTURE, MODEL, CHAIN, LEAF, ATOM
    }

    private StructureLevel level;
    private String identifier;
    private Atom atom;

    private List<PDBParsingTreeNode> children;

    public PDBParsingTreeNode(String identifier, StructureLevel level) {
        this.identifier = identifier;
        this.level = level;
        this.children = new ArrayList<>();
    }

    public PDBParsingTreeNode(String identifier, StructureLevel level, Atom atom) {
        this.level = level;
        this.identifier = identifier;
        this.atom = atom;
        this.children = null;
    }

    public void appendAtom(UniqueAtomIdentifer identifer, Atom atom) {
        ListIterator<PDBParsingTreeNode> iterator = this.children.listIterator();
        if (this.children.isEmpty()) {
            switch (this.level) {
                case STRUCTURE: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getModelIdentifer()), MODEL));
                    System.out.println(" Added Model: " + identifer.getModelIdentifer());
                    break;
                }
                case MODEL: {
                    iterator.add(new PDBParsingTreeNode(identifer.getChainIdentifer(), CHAIN));
                    System.out.println("  Added Chain: " + identifer.getChainIdentifer());
                    break;
                }
                case CHAIN: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF));
                    System.out.println("   Added Leaf: " + identifer.getLeafIdentifer());
                    break;
                }
                case LEAF: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                    System.out.println("    appending Atom: " + identifer.getAtomSerial());
                    return;
                }
                case ATOM: {
                    return;
                }
            }
            iterator.previous().appendAtom(identifer, atom);
        } else {
            switch (this.level) {
                case STRUCTURE: {
                    while (iterator.hasNext()) {
                        if (String.valueOf(identifer.getPdbIdentifer()).equals(this.identifier)) {
                            System.out.println("correct Structure going further");
                            iterator.next().appendAtom(identifer, atom);
                            return;
                        } else {
                            iterator.next();
                        }
                    }
                    break;
                }
                case MODEL: {
                    while (iterator.hasNext()) {
                        if (String.valueOf(identifer.getModelIdentifer()).equals(this.identifier)) {
                            System.out.println(" correct Model going further");
                            iterator.next().appendAtom(identifer, atom);
                            return;
                        } else {
                            iterator.next();
                        }
                    }
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getModelIdentifer()), MODEL));
                    System.out.println(" Added Model: " + identifer.getModelIdentifer());
                    break;

                }
                case CHAIN: {
                    while (iterator.hasNext()) {
                        if (String.valueOf(identifer.getChainIdentifer()).equals(this.identifier)) {
                            System.out.println("  correct Chain going further");
                            iterator.next().appendAtom(identifer, atom);
                            return;
                        } else {
                            iterator.next();
                        }
                    }
                    iterator.add(new PDBParsingTreeNode(identifer.getChainIdentifer(), CHAIN));
                    System.out.println("  Added Chain: " + identifer.getChainIdentifer());
                    break;
                }
                case LEAF: {
                    while (iterator.hasNext()) {
                        if (String.valueOf(identifer.getLeafIdentifer()).equals(this.identifier)) {
                            System.out.println("   correct Leaf, appending Atom: " + identifer.getAtomSerial());
                            iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                            return;
                        } else {
                            iterator.next();
                        }
                    }
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF));
                    System.out.println("   Added Leaf: " + identifer.getLeafIdentifer());
                    break;
                }
                case ATOM: {
                    return;
                }
            }
            iterator.previous().appendAtom(identifer, atom);
        }
    }

}
