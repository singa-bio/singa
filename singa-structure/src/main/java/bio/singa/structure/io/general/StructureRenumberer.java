package bio.singa.structure.io.general;

import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.structure.model.pdb.PdbLeafSubstructureFactory.createLeafSubstructure;

public class StructureRenumberer {

    private static final Logger logger = LoggerFactory.getLogger(StructureRenumberer.class);

    private static final String CHAIN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static Structure renumberAtomsConsecutively(Structure structure, boolean renumberChain) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberAtoms(structure, renumberChain);
    }

    /**
     * Renumbers the {@link LeafSubstructure}s in a given {@link PdbStructure} according to a renumbering map.
     * <b>Warning:</b>The method copies only the parts of the structure which are covered by the renumbering map.
     * Non-consecutive parts of the structure (ligands, etc.) are not affected and copied to the new structure.
     *
     * @param structure The {@link PdbStructure} to be renumbered.
     * @param renumberingMap The renumbering map, containing as key original {@link LeafIdentifier}s the renumbered
     * serial as values.
     * @return A copy of the structure, renumbered according to the given map.
     */
    public static Structure renumberLeaveSubstructuresWithMap(Structure structure, Map<PdbLeafIdentifier, Integer> renumberingMap) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberLeafSubstructures(((PdbStructure) structure), renumberingMap);

    }

    private int nextAtomIdentifier = 1;

    /**
     * maps old (pre renumbering) to new atom index (post renumbering)
     */
    private final Map<Integer, Integer> atomIdentifierMapping;

    private StructureRenumberer() {
        atomIdentifierMapping = new HashMap<>();
    }

    private PdbStructure renumberAtoms(Structure structure, boolean renumberChain) {
        logger.debug("Renumbering structure {} consecutively.", structure);
        PdbStructure renumberedStructure = new PdbStructure();
        renumberedStructure.setPdbIdentifier(structure.getStructureIdentifier());
        renumberedStructure.setTitle(structure.getTitle());
        for (Model model : structure.getAllModels()) {
            PdbModel renumberedModel = new PdbModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            int currentChainIndex = 0;
            Map<String, String> chainAliasMap = new HashMap<>();
            for (Chain chain : model.getAllChains()) {
                String chainIdentifier;
                if (renumberChain) {
                    chainIdentifier = String.valueOf(CHAIN_ALPHABET.charAt(currentChainIndex));
                    currentChainIndex++;
                } else {
                    chainIdentifier = chain.getChainIdentifier();
                }
                // remember which chain was mapped to which id
                chainAliasMap.put(chain.getChainIdentifier(), chainIdentifier);
                PdbChain renumberedChain = new PdbChain(chainIdentifier);
                renumberedModel.addChain(renumberedChain);
                if (chain instanceof PdbChain) {
                    PdbChain pdbChain = (PdbChain) chain;
                    for (PdbLeafSubstructure leafSubstructure : pdbChain.getConsecutivePart()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(chainIdentifier, leafSubstructure);
                        renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    }
                } else {
                    for (LeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(chainIdentifier, leafSubstructure);
                        renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    }
                }
                logger.trace("Keeping identifier {} for terminator token.", nextAtomIdentifier);
                nextAtomIdentifier++;

            }
            // nonconsecutive parts
            for (Chain chain : model.getAllChains()) {
                if (chain instanceof PdbChain) {
                    PdbChain pdbChain = (PdbChain) chain;
                    PdbChain renumberedChain = renumberedModel.getChain(chainAliasMap.get(chain.getChainIdentifier())).orElseThrow(NoSuchElementException::new);
                    for (PdbLeafSubstructure leafSubstructure : pdbChain.getNonConsecutivePart()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(renumberedChain.getChainIdentifier(), leafSubstructure);
                        renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    }
                }
            }
        }
        return renumberLinkEntries(renumberedStructure);
    }

    private PdbLeafSubstructure renumberAtomsInLeafSubstructure(String chainIdentifier, LeafSubstructure leafSubstructure) {
        LeafIdentifier identifier = leafSubstructure.getIdentifier();
        PdbLeafIdentifier pdbLeafIdentifier = new PdbLeafIdentifier(identifier.getStructureIdentifier(), identifier.getModelIdentifier(), chainIdentifier, identifier.getSerial(), identifier.getInsertionCode());
        PdbLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(pdbLeafIdentifier, leafSubstructure.getFamily());
        for (Atom atom : leafSubstructure.getAllAtoms()) {
            PdbAtom renumberedAtom = new PdbAtom(nextAtomIdentifier, atom.getElement(), atom.getAtomName(), atom.getPosition());
            renumberedAtom.setBFactor(atom.getBFactor());
            // conserve identifier
            logger.trace("Renumbering atom {} to {}.", atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
            atomIdentifierMapping.put(atom.getAtomIdentifier(), renumberedAtom.getAtomIdentifier());
            renumberedLeafSubstructure.addAtom(renumberedAtom);
            nextAtomIdentifier++;
        }
        if (leafSubstructure instanceof PdbLeafSubstructure) {
            PdbLeafSubstructure pdbLeafSubstructure = (PdbLeafSubstructure) leafSubstructure;
            for (PdbBond edge : pdbLeafSubstructure.getBonds()) {
                PdbBond edgeCopy = edge.getCopy();
                PdbAtom source = renumberedLeafSubstructure.getAtom(atomIdentifierMapping.get(edge.getSource().getAtomIdentifier())).get();
                PdbAtom target = renumberedLeafSubstructure.getAtom(atomIdentifierMapping.get(edge.getTarget().getAtomIdentifier())).get();
                renumberedLeafSubstructure.addBondBetween(edgeCopy, source, target);
            }
        }
        return renumberedLeafSubstructure;
    }

    private PdbStructure renumberLinkEntries(PdbStructure structure) {
        if (structure.getLinkEntries() == null) {
            return structure;
        }
        for (PdbLinkEntry linkEntry : structure.getLinkEntries()) {
            Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> firstAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getFirstAtom().getAtomIdentifier()));
            Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> secondAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getSecondAtom().getAtomIdentifier()));
            if (firstAtomEntry.isPresent() && secondAtomEntry.isPresent()) {
                Atom firstAtom = firstAtomEntry.get().getValue();
                UniqueAtomIdentifier firstUniqueAtomIdentifier = firstAtomEntry.get().getKey();
                LeafSubstructure firstLeaf = structure.getLeafSubstructure(firstUniqueAtomIdentifier.getLeafIdentifier()).get();
                Atom secondAtom = secondAtomEntry.get().getValue();
                UniqueAtomIdentifier secondUniqueAtomIdentifier = secondAtomEntry.get().getKey();
                LeafSubstructure secondLeaf = structure.getLeafSubstructure(secondUniqueAtomIdentifier.getLeafIdentifier()).get();
                structure.addLinkEntry((new PdbLinkEntry(firstLeaf, firstAtom, secondLeaf, secondAtom)));
            }
        }
        return structure;
    }

    private PdbStructure renumberLeafSubstructures(PdbStructure structure, Map<PdbLeafIdentifier, Integer> renumberingMap) {
        PdbStructure renumberedStructure = new PdbStructure();
        renumberedStructure.setPdbIdentifier(structure.getStructureIdentifier());
        for (Model model : structure.getAllModels()) {
            PdbModel renumberedModel = new PdbModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                PdbChain oakChain = (PdbChain) chain;
                PdbChain renumberedChain = new PdbChain(chain.getChainIdentifier());
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
                    PdbLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(renumberedIdentifier, leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    copyAtomsInLeafSubstructure(leafSubstructure, renumberedLeafSubstructure);
                }
            }
            // nonconsecutive parts are copied without renumbering
            for (Chain chain : model.getAllChains()) {
                PdbChain oakChain = (PdbChain) chain;
                PdbChain renumberedChain = (PdbChain) renumberedModel.getChain(chain.getChainIdentifier()).orElseThrow(NoSuchElementException::new);
                for (PdbLeafSubstructure leafSubstructure : oakChain.getNonConsecutivePart()) {
                    PdbLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(true);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    copyAtomsInLeafSubstructure(leafSubstructure, renumberedLeafSubstructure);
                }
            }
        }
        return renumberLinkEntries(renumberedStructure);
    }

    private void copyAtomsInLeafSubstructure(LeafSubstructure leafSubstructure, PdbLeafSubstructure renumberedLeafSubstructure) {
        for (Atom atom : leafSubstructure.getAllAtoms()) {
            PdbAtom renumberedAtom = new PdbAtom(
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
