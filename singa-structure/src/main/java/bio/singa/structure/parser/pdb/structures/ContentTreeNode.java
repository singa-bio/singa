package bio.singa.structure.parser.pdb.structures;

import bio.singa.features.identifiers.UniqueAtomIdentifer;
import bio.singa.structure.model.oak.OakAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.structure.parser.pdb.structures.ContentTreeNode.StructureLevel.*;

/**
 * A tree-like structure holding information about atoms, their leafs, chains and models.
 *
 * @author cl
 */
class ContentTreeNode {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContentTreeNode.class);
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
    private OakAtom atom;
    /**
     * The children of this node.
     */
    private List<ContentTreeNode> children;

    /**
     * Creates a new content tree node.
     *
     * @param identifier The identifier.
     * @param level The structural level.
     */
    ContentTreeNode(String identifier, StructureLevel level) {
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
    private ContentTreeNode(String identifier, OakAtom atom) {
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
    private ContentTreeNode(String identifier, char insertionCode) {
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
    public OakAtom getAtom() {
        return atom;
    }

    /**
     * Sets the atom associated to this node.
     *
     * @param atom The atom.
     */
    public void setAtom(OakAtom atom) {
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
    public List<ContentTreeNode> getNodesFromLevel(StructureLevel level) {
        List<ContentTreeNode> nodes = new ArrayList<>();
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
    public Map<String, OakAtom> getAtomMap() {
        if (getLevel() == LEAF) {
            Map<String, OakAtom> atoms = new HashMap<>();
            for (ContentTreeNode node : children) {
                atoms.put(node.getAtom().getAtomName(), node.getAtom());
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
    public void appendAtom(OakAtom atom, UniqueAtomIdentifer identifer) {
        ListIterator<ContentTreeNode> iterator = children.listIterator();
        if (children.isEmpty()) {
            switch (level) {
                case STRUCTURE: {
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getModelIdentifier()), MODEL));
                    logger.trace("Added model {}", identifer.getModelIdentifier());
                    break;
                }
                case MODEL: {
                    iterator.add(new ContentTreeNode(identifer.getChainIdentifier(), CHAIN));
                    logger.trace("Added chain {}", identifer.getChainIdentifier());
                    break;
                }
                case CHAIN: {
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getLeafSerial()), identifer.getLeafInsertionCode()));
                    logger.trace("Added leaf {}", identifer.getLeafSerial());
                    break;
                }
                case LEAF: {
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
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
                        ContentTreeNode model = iterator.next();
                        if (model.identifier.equals(String.valueOf(identifer.getModelIdentifier()))) {
                            logger.trace("Already at correct model, going further.");
                            model.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getModelIdentifier()), MODEL));
                    logger.trace("Added model {}", identifer.getModelIdentifier());
                    break;
                }
                case MODEL: {
                    while (iterator.hasNext()) {
                        ContentTreeNode chain = iterator.next();
                        if (chain.identifier.equals(String.valueOf(identifer.getChainIdentifier()))) {
                            logger.trace("Already at correct chain, going further.");
                            chain.appendAtom(atom, identifer);
                            return;
                        }
                    }
                    iterator.add(new ContentTreeNode(identifer.getChainIdentifier(), CHAIN));
                    logger.trace("Added chain {}", identifer.getChainIdentifier());
                    break;
                }
                case CHAIN: {
                    while (iterator.hasNext()) {
                        ContentTreeNode leaf = iterator.next();
                        if (leaf.identifier.equals(String.valueOf(identifer.getLeafSerial())) && leaf.insertionCode == identifer.getLeafInsertionCode()) {
                            logger.trace("Found correct leaf, appending atom {}", identifer.getAtomSerial());
                            leaf.children.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
                            return;
                        }
                    }
                    logger.trace("Added leaf {} with initial atom {} ", identifer.getLeafSerial(), identifer.getAtomSerial());
                    ContentTreeNode leafNode = new ContentTreeNode(String.valueOf(identifer.getLeafSerial()), identifer.getLeafInsertionCode());
                    leafNode.children.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), atom));
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

    /**
     * The level of any atom in the tree.
     */
    public enum StructureLevel {
        STRUCTURE, MODEL, CHAIN, LEAF, ATOM
    }

}
