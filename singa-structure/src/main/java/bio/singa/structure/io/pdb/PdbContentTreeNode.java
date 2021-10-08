package bio.singa.structure.io.pdb;

import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.pdb.PdbAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.structure.io.pdb.PdbContentTreeNode.StructureLevel.*;

/**
 * A tree-like structure holding information about atoms, their leaves, chains and models.
 *
 * @author cl
 */
class PdbContentTreeNode {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PdbContentTreeNode.class);
    /**
     * The level of this node.
     */
    private final StructureLevel level;
    /**
     * The identifying string of this node.
     */
    private String identifier;
    /**
     * The insertion code if this node is on leaf level.
     */
    private char insertionCode;
    /**
     * The actual atom.
     */
    private PdbAtom atom;
    /**
     * The children of this node.
     */
    private List<PdbContentTreeNode> children;

    /**
     * Creates a new content tree node.
     *
     * @param identifier The identifier.
     * @param level The structural level.
     */
    PdbContentTreeNode(String identifier, StructureLevel level) {
        this.identifier = identifier;
        this.level = level;
        children = new ArrayList<>();
    }

    /**
     * Creates a new content tree node on the deepest (atom) level.
     *
     * @param identifier The identifier.
     * @param atom The atom.
     */
    private PdbContentTreeNode(String identifier, PdbAtom atom) {
        this.identifier = identifier;
        level = ATOM;
        this.atom = atom;
    }

    /**
     * Creates a new content tree node on the leaf level with an insertion code.
     *
     * @param identifier The identifier.
     * @param insertionCode The insertion code.
     */
    private PdbContentTreeNode(String identifier, char insertionCode) {
        this(identifier, LEAF);
        this.insertionCode = insertionCode;
    }

    /**
     * Returns the level of this node.
     *
     * @return The level of this node.
     */
    public StructureLevel getLevel() {
        return level;
    }

    /**
     * Returns the identifier of this node.
     *
     * @return The identifier of this node.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the atom associated to this node.
     *
     * @return The atom associated to this node.
     */
    public PdbAtom getAtom() {
        return atom;
    }

    /**
     * Sets the atom associated to this node.
     *
     * @param atom The atom.
     */
    public void setAtom(PdbAtom atom) {
        this.atom = atom;
    }

    /**
     * Returns the insertion code if any is associated to this node.
     *
     * @return The insertion code if any is associated to this node.
     */
    public char getInsertionCode() {
        return insertionCode;
    }

    /**
     * Returns all nodes with the given level.
     *
     * @param level The structural level.
     * @return All nodes with the given level.
     */
    public List<PdbContentTreeNode> getNodesFromLevel(StructureLevel level) {
        List<PdbContentTreeNode> nodes = new ArrayList<>();
        if (this.level == level) {
            nodes.add(this);
        } else {
            children.forEach(child -> nodes.addAll(child.getNodesFromLevel(level)));
        }
        return nodes;
    }

    /**
     * Returns all atoms from this node but only if is on the leaf level.
     *
     * @return All atoms from this node
     */
    public Map<String, PdbAtom> getAtomMap() {
        if (getLevel() == LEAF) {
            Map<String, PdbAtom> atoms = new HashMap<>();
            for (PdbContentTreeNode node : children) {
                atoms.put(node.getAtom().getAtomName(), node.getAtom());
            }
            return atoms;
        }
        return null;
    }

    public Set<PdbAtom> getAtoms(){
        if (getLevel() == LEAF) {
            Set<PdbAtom> atoms = new HashSet<>();
            for (PdbContentTreeNode node : children) {
                atoms.add(node.getAtom());
            }
            return atoms;
        }
        return null;
    }

    /**
     * Adds an atom to this tree, creating in between nodes if necessary.
     *
     * @param atom The atom to add.
     * @param identifer Its identifier.
     */
    public void appendAtom(PdbAtom atom, UniqueAtomIdentifier identifer) {
        ListIterator<PdbContentTreeNode> iterator = children.listIterator();
        if (children.isEmpty()) {
            switch (level) {
                case STRUCTURE: {
                    iterator.add(new PdbContentTreeNode(String.valueOf(identifer.getLeafIdentifier().getModelIdentifier()), MODEL));
                    logger.trace("Added model {}", identifer.getLeafIdentifier().getModelIdentifier());
                    break;
                }
                case MODEL: {
                    iterator.add(new PdbContentTreeNode(identifer.getLeafIdentifier().getChainIdentifier(), CHAIN));
                    logger.trace("Added chain {}", identifer.getLeafIdentifier().getChainIdentifier());
                    break;
                }
                case CHAIN: {
                    iterator.add(new PdbContentTreeNode(String.valueOf(identifer.getLeafIdentifier().getSerial()), identifer.getLeafIdentifier().getInsertionCode()));
                    logger.trace("Added leaf {}", identifer.getLeafIdentifier().getSerial());
                    break;
                }
                case LEAF: {
                    iterator.add(new PdbContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
                    logger.trace("Appending atom {}", identifer.getAtomSerial());
                    return;
                }
                case ATOM: {
                    return;
                }
            }
            iterator.previous().appendAtom(atom, identifer);
        } else {
            switch (level) {
                case STRUCTURE: {
                    while (iterator.hasNext()) {
                        PdbContentTreeNode model = iterator.next();
                        if (model.identifier.equals(String.valueOf(identifer.getLeafIdentifier().getModelIdentifier()))) {
                            logger.trace("Already at correct model, going further.");
                            model.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new PdbContentTreeNode(String.valueOf(identifer.getLeafIdentifier().getModelIdentifier()), MODEL));
                    logger.trace("Added model {}", identifer.getLeafIdentifier().getModelIdentifier());
                    break;
                }
                case MODEL: {
                    while (iterator.hasNext()) {
                        PdbContentTreeNode chain = iterator.next();
                        if (chain.identifier.equals(String.valueOf(identifer.getLeafIdentifier().getChainIdentifier()))) {
                            logger.trace("Already at correct chain, going further.");
                            chain.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new PdbContentTreeNode(identifer.getLeafIdentifier().getChainIdentifier(), CHAIN));
                    logger.trace("Added chain {}", identifer.getLeafIdentifier().getChainIdentifier());
                    break;
                }
                case CHAIN: {
                    while (iterator.hasNext()) {
                        PdbContentTreeNode leaf = iterator.next();
                        if (leaf.identifier.equals(String.valueOf(identifer.getLeafIdentifier().getSerial())) && leaf.insertionCode == identifer.getLeafIdentifier().getInsertionCode()) {
                            logger.trace("Found correct leaf, appending atom {}", identifer.getAtomSerial());
                            leaf.children.add(new PdbContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
                            return;
                        }
                    }
                    logger.trace("Added leaf {} with initial atom {} ", identifer.getLeafIdentifier().getSerial(), identifer.getAtomSerial());
                    PdbContentTreeNode leafNode = new PdbContentTreeNode(String.valueOf(identifer.getLeafIdentifier().getSerial()), identifer.getLeafIdentifier().getInsertionCode());
                    leafNode.children.add(new PdbContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
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

    @Override
    public String toString() {
        return "ContentTreeNode{" +
                "level=" + level +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    /**
     * The level of any atom in the tree.
     */
    public enum StructureLevel {
        STRUCTURE, MODEL, CHAIN, LEAF, ATOM
    }

}
