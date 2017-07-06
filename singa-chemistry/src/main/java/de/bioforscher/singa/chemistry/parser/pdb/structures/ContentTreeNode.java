package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.model.UniqueAtomIdentifer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.bioforscher.singa.chemistry.parser.pdb.structures.ContentTreeNode.StructureLevel.*;

/**
 * @author cl
 */
public class ContentTreeNode {

    private static final Logger logger = LoggerFactory.getLogger(ContentTreeNode.class);

    public enum StructureLevel {
        STRUCTURE, MODEL, CHAIN, LEAF, ATOM
    }

    private StructureLevel level;
    private String identifier;
    private Map<Atom, UniqueAtomIdentifer> identiferMap;
    private Atom atom;

    private List<ContentTreeNode> children;

    public ContentTreeNode(String identifier, StructureLevel level) {
        this.identifier = identifier;
        this.level = level;
        this.children = new ArrayList<>();
        this.identiferMap = new HashMap<>();
    }

    public ContentTreeNode(String identifier, StructureLevel level, Atom atom) {
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

    public List<ContentTreeNode> getChildren() {
        return this.children;
    }

    public List<ContentTreeNode> getNodesFromLevel(StructureLevel level) {
        List<ContentTreeNode> nodes = new ArrayList<>();
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
            for (ContentTreeNode node: this.children) {
                atoms.put(node.getAtom().getAtomNameString(), node.getAtom());
            }
            return atoms;
        }
        return null;
    }

    public void appendAtom(Atom atom, UniqueAtomIdentifer identifer) {
        ListIterator<ContentTreeNode> iterator = this.children.listIterator();
        if (this.children.isEmpty()) {
            switch (this.level) {
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
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF));
                    logger.trace("Added leaf {}", identifer.getLeafIdentifer());
                    break;
                }
                case LEAF: {
                    iterator.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                    logger.trace("Appending atom {}", identifer.getAtomSerial());
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
                        if (leaf.identifier.equals(String.valueOf(identifer.getLeafIdentifer()))) {
                            logger.trace("Found correct leaf, appending atom {}",identifer.getAtomSerial());
                            leaf.children.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
                            leaf.identiferMap.put(atom, identifer);
                            return;
                        }
                    }
                    logger.trace("Added leaf {} with initial atom {} ", identifer.getLeafIdentifer(), identifer.getAtomSerial());
                    ContentTreeNode leafNode = new ContentTreeNode(String.valueOf(identifer.getLeafIdentifer()), LEAF);
                    leafNode.identiferMap.put(atom, identifer);
                    leafNode.children.add(new ContentTreeNode(String.valueOf(identifer.getAtomSerial()), ATOM, atom));
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
