package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;

import java.util.*;

import static de.bioforscher.chemistry.parser.pdb.structures.PDBParsingTreeNode.StructureLevel.*;

/**
 * @author cl
 */
public class PDBParsingTreeNode {

    public enum StructureLevel {
        STRUCTURE, MODEL, CHAIN, LEAF, ATOM
    }

    private StructureLevel level;
    private String identifier;
    private Map<Atom, UniqueAtomIdentifer> identiferMap;
    private Atom atom;

    private List<PDBParsingTreeNode> children;

    public PDBParsingTreeNode(String identifier, StructureLevel level) {
        this.identifier = identifier;
        this.level = level;
        this.children = new ArrayList<>();
        this.identiferMap = new HashMap<>();
    }

    public PDBParsingTreeNode(String identifier, StructureLevel level, Atom atom) {
        this.level = level;
        this.identifier = identifier;
        this.atom = atom;
        this.children = null;
    }

    public StructureLevel getLevel() {
        return this.level;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Atom getAtom() {
        return this.atom;
    }

    public void setAtom(Atom atom) {
        this.atom = atom;
    }

    public List<PDBParsingTreeNode> getChildren() {
        return this.children;
    }

    public List<PDBParsingTreeNode> getNodesFromLevel(StructureLevel level) {
        List<PDBParsingTreeNode> nodes = new ArrayList<>();
        if (this.level == level) {
            nodes.add(this);
        } else {
            this.children.forEach(child -> nodes.addAll(child.getNodesFromLevel(level)));
        }
        return nodes;
    }

    public Map<String, Atom> getAtomMap() {
        if (this.getLevel() == LEAF) {
            Map<String, Atom> atoms = new HashMap<>();
            for (PDBParsingTreeNode node: this.children) {
                atoms.put(node.getAtom().getAtomNameString(), node.getAtom());
            }
            return atoms;
        }
        return null;
    }

    public void appendAtom(Atom atom, UniqueAtomIdentifer identifer) {
        ListIterator<PDBParsingTreeNode> iterator = this.children.listIterator();
        if (this.children.isEmpty()) {
            switch (this.level) {
                case STRUCTURE: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getModelIdentifer()), MODEL));
                    // System.out.println(" Added Model: " + identifer.getModelIdentifer());
                    break;
                }
                case MODEL: {
                    iterator.add(new PDBParsingTreeNode(identifer.getChainIdentifer(), CHAIN));
                    // System.out.println("  Added Chain: " + identifer.getChainIdentifer());
                    break;
                }
                case CHAIN: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF));
                    // System.out.println("   Added Leaf: " + identifer.getLeafIdentifer());
                    break;
                }
                case LEAF: {
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                    // System.out.println("    appending Atom: " + identifer.getAtomSerial());
                    return;
                }
                case ATOM: {
                    return;
                }
            }
            iterator.previous().appendAtom(atom, identifer);
        } else {
            switch (this.level) {
                case STRUCTURE: {
                    while (iterator.hasNext()) {
                        PDBParsingTreeNode model = iterator.next();
                        if (model.identifier.equals(String.valueOf(identifer.getModelIdentifer()))) {
                            // System.out.println(" correct model going further");
                            model.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new PDBParsingTreeNode(String.valueOf(identifer.getModelIdentifer()), MODEL));
                    // System.out.println(" Added Model: " + identifer.getModelIdentifer());
                    break;
                }
                case MODEL: {
                    while (iterator.hasNext()) {
                        PDBParsingTreeNode chain = iterator.next();
                        if (chain.identifier.equals(String.valueOf(identifer.getChainIdentifer()))) {
                            // System.out.println("  correct chain going further");
                            chain.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new PDBParsingTreeNode(identifer.getChainIdentifer(), CHAIN));
                    // System.out.println("  Added Chain: " + identifer.getChainIdentifer());
                    break;
                }
                case CHAIN: {
                    while (iterator.hasNext()) {
                        PDBParsingTreeNode leaf = iterator.next();
                        if (leaf.identifier.equals(String.valueOf(identifer.getLeafIdentifer()))) {
                            // System.out.println("   correct leaf, appending atom:"+identifer.getAtomSerial());
                            leaf.children.add(new PDBParsingTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                            leaf.identiferMap.put(atom, identifer);
                            return;
                        }
                    }
                    // System.out.println("   added Leaf: " + identifer.getLeafIdentifer());
                    PDBParsingTreeNode leafNode = new PDBParsingTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF);
                    leafNode.identiferMap.put(atom, identifer);
                    // System.out.println("    appending Atom: " + identifer.getAtomSerial());
                    leafNode.children.add(new PDBParsingTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                    iterator.add(leafNode);
                    break;
                }
                case LEAF:
                case ATOM: {
                    return;
                }
            }
            iterator.previous().appendAtom(atom, identifer);
        }
    }

    public Map<Atom, UniqueAtomIdentifer> getIdentiferMap() {
        return this.identiferMap;
    }
}
