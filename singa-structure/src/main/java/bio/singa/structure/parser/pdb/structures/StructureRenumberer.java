package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.oak.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.oak.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.structure.model.oak.LeafSubstructureFactory.createLeafSubstructure;

public class StructureRenumberer {

    private static final Logger logger = LoggerFactory.getLogger(StructureRenumberer.class);

    public static Structure renumberAtomsConsecutively(Structure structure) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberAtoms(((OakStructure) structure));
    }

    /**
     * Renumbers the {@link LeafSubstructure}s in a given {@link OakStructure} according to a renumbering map.
     * <b>Warning:</b>The method copies only the parts of the structure which are covered by the renumbering map.
     * Non-consecutive parts of the structure (ligands, etc.) are not affected and copied to the new structure.
     *
     * @param structure The {@link OakStructure} to be renumbered.
     * @param renumberingMap The renumbering map, containing as key original {@link LeafIdentifier}s the renumbered
     * serial as values.
     * @return A copy of the structure, renumbered according to the given map.
     */
    public static Structure renumberLeaveSubstructuresWithMap(Structure structure, Map<PdbLeafIdentifier, Integer> renumberingMap) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberLeafSubstructures(((OakStructure) structure), renumberingMap);

    }

    private int nextAtomIdentifier = 1;

    /**
     * maps old (pre renumbering) to new atom index (post renumbering)
     */
    private final Map<Integer, Integer> atomIdentifierMapping;

    private StructureRenumberer() {
        atomIdentifierMapping = new HashMap<>();
    }

    private OakStructure renumberAtoms(OakStructure structure) {
        logger.debug("Renumbering structure {} consecutively.", structure);
        OakStructure renumberedStructure = new OakStructure();
        renumberedStructure.setPdbIdentifier(structure.getPdbIdentifier());
        renumberedStructure.setTitle(structure.getTitle());
        for (Model model : structure.getAllModels()) {
            OakModel renumberedModel = new OakModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = new OakChain(chain.getChainIdentifier());
                renumberedModel.addChain(renumberedChain);
                for (OakLeafSubstructure leafSubstructure : oakChain.getConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(leafSubstructure);
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                }
                logger.trace("Keeping identifier {} for terminator token.", nextAtomIdentifier);
                nextAtomIdentifier++;
            }
            // nonconsecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = (OakChain) renumberedModel.getChain(chain.getChainIdentifier()).orElseThrow(NoSuchElementException::new);
                for (OakLeafSubstructure leafSubstructure : oakChain.getNonConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(leafSubstructure);
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(true);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                }
            }
        }
        return renumberLinkEntries(renumberedStructure);
    }

    private OakLeafSubstructure renumberAtomsInLeafSubstructure(OakLeafSubstructure leafSubstructure) {
        OakLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
        for (Atom atom : leafSubstructure.getAllAtoms()) {
            OakAtom renumberedAtom = new OakAtom(nextAtomIdentifier, atom.getElement(), atom.getAtomName(), atom.getPosition());
            renumberedAtom.setBFactor(atom.getBFactor());
            // conserve identifier
            logger.trace("Renumbering atom {} to {}.", atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
            atomIdentifierMapping.put(atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
            renumberedLeafSubstructure.addAtom(renumberedAtom);
            nextAtomIdentifier++;
        }
        for (OakBond edge : ((OakLeafSubstructure) leafSubstructure).getBonds()) {
            OakBond edgeCopy = edge.getCopy();
            OakAtom source = ((OakAtom) renumberedLeafSubstructure.getAtom(atomIdentifierMapping.get(edge.getSource().getAtomIdentifier())).get());
            OakAtom target = ((OakAtom) renumberedLeafSubstructure.getAtom(atomIdentifierMapping.get(edge.getTarget().getAtomIdentifier())).get());
            renumberedLeafSubstructure.addBondBetween(edgeCopy, source, target);
        }
        return renumberedLeafSubstructure;
    }

    private OakStructure renumberLinkEntries(OakStructure structure) {
        if (structure.getLinkEntries() == null) {
            return structure;
        }
        for (LinkEntry linkEntry : structure.getLinkEntries()) {
            Optional<Map.Entry<UniqueAtomIdentifier, Atom>> firstAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getFirstAtom().getAtomIdentifier()));
            Optional<Map.Entry<UniqueAtomIdentifier, Atom>> secondAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getSecondAtom().getAtomIdentifier()));
            if (firstAtomEntry.isPresent() && secondAtomEntry.isPresent()) {
                Atom firstAtom = firstAtomEntry.get().getValue();
                UniqueAtomIdentifier firstUniqueAtomIdentifier = firstAtomEntry.get().getKey();
                LeafSubstructure firstLeaf = structure.getLeafSubstructure(firstUniqueAtomIdentifier.getLeafIdentifier()).get();
                Atom secondAtom = secondAtomEntry.get().getValue();
                UniqueAtomIdentifier secondUniqueAtomIdentifier = secondAtomEntry.get().getKey();
                LeafSubstructure secondLeaf = structure.getLeafSubstructure(secondUniqueAtomIdentifier.getLeafIdentifier()).get();
                structure.addLinkEntry((new LinkEntry(firstLeaf, firstAtom, secondLeaf, secondAtom)));
            }
        }
        return structure;
    }

    private OakStructure renumberLeafSubstructures(OakStructure structure, Map<PdbLeafIdentifier, Integer> renumberingMap) {
        OakStructure renumberedStructure = new OakStructure();
        renumberedStructure.setPdbIdentifier(structure.getPdbIdentifier());
        for (Model model : structure.getAllModels()) {
            OakModel renumberedModel = new OakModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = new OakChain(chain.getChainIdentifier());
                renumberedModel.addChain(renumberedChain);
                for (LeafSubstructure leafSubstructure : oakChain.getConsecutivePart()) {
                    PdbLeafIdentifier originalIdentifier = ((PdbLeafIdentifier) leafSubstructure.getIdentifier());
                    // skip leaves not specified in the renumbering map
                    if (!renumberingMap.containsKey(originalIdentifier)) {
                        continue;
                    }
                    PdbLeafIdentifier renumberedIdentifier = new PdbLeafIdentifier(
                            originalIdentifier.getStructureIdentifier(),
                            originalIdentifier.getModelIdentifier(),
                            originalIdentifier.getChainIdentifier(),
                            renumberingMap.get(originalIdentifier),
                            originalIdentifier.getInsertionCode());
                    OakLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(renumberedIdentifier, leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    copyAtomsInLeafSubstructure(leafSubstructure, renumberedLeafSubstructure);
                }
            }
            // nonconsecutive parts are copied without renumbering
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = (OakChain) renumberedModel.getChain(chain.getChainIdentifier()).orElseThrow(NoSuchElementException::new);
                for (OakLeafSubstructure leafSubstructure : oakChain.getNonConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(true);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    copyAtomsInLeafSubstructure(leafSubstructure, renumberedLeafSubstructure);
                }
            }
        }
        return renumberLinkEntries(renumberedStructure);
    }

    private void copyAtomsInLeafSubstructure(LeafSubstructure leafSubstructure, OakLeafSubstructure renumberedLeafSubstructure) {
        for (Atom atom : leafSubstructure.getAllAtoms()) {
            OakAtom renumberedAtom = new OakAtom(
                    atom.getAtomIdentifier(),
                    atom.getElement(),
                    atom.getAtomName(),
                    atom.getPosition());
            renumberedAtom.setBFactor(atom.getBFactor());
            renumberedLeafSubstructure.addAtom(renumberedAtom);
            atomIdentifierMapping.put(atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
        }
    }

}
