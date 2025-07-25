package bio.singa.structure.io.general;

import bio.singa.structure.model.cif.CifBond;
import bio.singa.structure.model.cif.CifChain;
import bio.singa.structure.model.cif.CifLeafSubstructure;
import bio.singa.structure.model.general.AuthLeafIdentifier;
import bio.singa.structure.model.general.LinkEntry;
import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static Structure renumberLeaveSubstructuresWithMap(Structure structure, Map<AuthLeafIdentifier, Integer> renumberingMap) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberLeafSubstructures(((PdbStructure) structure), renumberingMap);

    }

    public static Structure renumberEverything(Structure structure) {
        StructureRenumberer structureRenumberer = new StructureRenumberer();
        return structureRenumberer.renumberEverythingFor(structure);

    }

    private int nextAtomIdentifier = 1;

    /**
     * maps old (pre renumbering) to new atom index (post renumbering)
     */
    private final Map<Integer, Integer> atomIdentifierMapping;


    private StructureRenumberer() {
        atomIdentifierMapping = new HashMap<>();
    }


    private PdbStructure renumberEverythingFor(Structure structure) {
        logger.debug("Renumbering structure {} consecutively.", structure);

        PdbStructure renumberedStructure = new PdbStructure();
        renumberedStructure.setPdbIdentifier(structure.getStructureIdentifier());
        renumberedStructure.setTitle(structure.getTitle());

        Map<String, AtomicInteger> latestSubstructureIdentifier = new HashMap<>();

        for (Model model : structure.getAllModels()) {
            PdbModel renumberedModel = new PdbModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            int currentChainIndex = 0;

            List<? extends Chain> chainList = new ArrayList<Chain>(model.getAllChains());
            chainList.sort(Comparator.comparing((Chain chain) -> StructureRenumberer.numberOfAminoAcids(chain))
                    .thenComparing(Chain::getNumberOfLeafSubstructures).reversed());

            for (Chain chain : chainList) {
                String renumberedChainIdentifier;
                renumberedChainIdentifier = String.valueOf(CHAIN_ALPHABET.charAt(currentChainIndex));
                if (currentChainIndex >= CHAIN_ALPHABET.length() - 1) {
                    logger.warn("trying to generate more chains than there are available chain identifiers, all" +
                            " remaining chains will be combined to chain z");
                } else {
                    currentChainIndex++;
                }

                PdbChain renumberedChain = new PdbChain(renumberedChainIdentifier);
                // remember id of the latest substructure beginning with 1
                if (!latestSubstructureIdentifier.containsKey(renumberedChainIdentifier)) {
                    latestSubstructureIdentifier.put(renumberedChainIdentifier, new AtomicInteger(1));
                }
                renumberedModel.addChain(renumberedChain);

                for (LeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {

                    LeafIdentifier originalIdentifier = leafSubstructure.getIdentifier();
                    AuthLeafIdentifier renumberedIdentifier = new AuthLeafIdentifier(
                            originalIdentifier.getStructureIdentifier(),
                            originalIdentifier.getModelIdentifier(),
                            renumberedChainIdentifier,
                            latestSubstructureIdentifier.get(renumberedChainIdentifier).getAndIncrement());

                    PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(leafSubstructure, renumberedIdentifier);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                }
            }
            logger.trace("Keeping identifier {} for terminator token.", nextAtomIdentifier);
            nextAtomIdentifier++;

        }

        return renumberLinkEntries(renumberedStructure);
    }

    private static <ChainType extends Chain> int numberOfAminoAcids(ChainType chain) {
        return chain.getAllAminoAcids().size();
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
            // TODO implement setting on how to sort
            List<? extends Chain> chainList = new ArrayList<Chain>(model.getAllChains());
            chainList.sort(Comparator.comparing(Chain::getNumberOfLeafSubstructures).reversed());

            for (Chain chain : chainList) {
                String chainIdentifier;
                if (renumberChain) {
                    chainIdentifier = String.valueOf(CHAIN_ALPHABET.charAt(currentChainIndex));
                    if (currentChainIndex >= CHAIN_ALPHABET.length() - 1) {
                        logger.warn("trying to generate more chains than there are available chain identifiers, all" +
                                " remaining chains will be combined to chain z");
                    } else {
                        currentChainIndex++;
                    }
                } else {
                    // ensure PDB/auth-style naming
                    chainIdentifier = chain instanceof CifChain ? ((CifChain) chain).getLegacyIdentifier() : chain.getChainIdentifier();
                }
                // remember which chain was mapped to which id
                chainAliasMap.put(chain.getChainIdentifier(), chainIdentifier);
                PdbChain renumberedChain; // 'merge' chains is multiple label_asym_id map to the same auth_asym_id
                if (renumberedModel.getAllChainIdentifiers().contains(chainIdentifier)) {
                    renumberedChain = renumberedModel.getChain(chainIdentifier).get();
                } else {
                    renumberedChain = new PdbChain(chainIdentifier);
                    renumberedModel.addChain(renumberedChain);
                }
                if (chain instanceof PdbChain) {
                    PdbChain pdbChain = (PdbChain) chain;
                    for (PdbLeafSubstructure leafSubstructure : pdbChain.getConsecutivePart()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(chainIdentifier, leafSubstructure);
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    }
                } else {
                    for (LeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(chainIdentifier, leafSubstructure);
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    }
                }
                logger.trace("Keeping identifier {} for terminator token.", nextAtomIdentifier);
                nextAtomIdentifier++;

            }
            // nonconsecutive parts
            for (Chain chain : chainList) {
                if (chain instanceof PdbChain) {
                    PdbChain pdbChain = (PdbChain) chain;
                    PdbChain renumberedChain = renumberedModel.getChain(chainAliasMap.get(chain.getChainIdentifier())).orElseThrow(NoSuchElementException::new);
                    for (PdbLeafSubstructure leafSubstructure : pdbChain.getNonConsecutivePart()) {
                        PdbLeafSubstructure renumberedLeafSubstructure = renumberAtomsInLeafSubstructure(renumberedChain.getChainIdentifier(), leafSubstructure);
                        renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    }
                }
            }
        }
        return renumberLinkEntries(renumberedStructure);
    }

    private PdbLeafSubstructure renumberAtomsInLeafSubstructure(String chainIdentifier, LeafSubstructure leafSubstructure) {
        LeafIdentifier identifier = leafSubstructure.getAuthIdentifier();
        AuthLeafIdentifier authLeafIdentifier = new AuthLeafIdentifier(identifier.getStructureIdentifier(), identifier.getModelIdentifier(), chainIdentifier, identifier.getSerial(), identifier.getInsertionCode());
        return renumberAtomsInLeafSubstructure(leafSubstructure, authLeafIdentifier);
    }

    private PdbLeafSubstructure renumberAtomsInLeafSubstructure(LeafSubstructure leafSubstructure, AuthLeafIdentifier authLeafIdentifier) {
        PdbLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(authLeafIdentifier, leafSubstructure.getFamily());
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
                Integer i1 = atomIdentifierMapping.get(edge.getSource().getAtomIdentifier());
                if (i1 == null) continue;
                Integer i2 = atomIdentifierMapping.get(edge.getTarget().getAtomIdentifier());
                if (i2 == null) continue;
                Optional<PdbAtom> atom1 = renumberedLeafSubstructure.getAtom(i1);
                Optional<PdbAtom> atom2 = renumberedLeafSubstructure.getAtom(i2);
                if (!atom1.isPresent() || !atom2.isPresent()) continue;

                PdbBond edgeCopy = edge.getCopy();
                renumberedLeafSubstructure.addBondBetween(edgeCopy, atom1.get(), atom2.get());
            }
        } else if (leafSubstructure instanceof CifLeafSubstructure) {
            CifLeafSubstructure cifLeafSubstructure = (CifLeafSubstructure) leafSubstructure;
            for (CifBond edge : cifLeafSubstructure.getBonds()) {
                Integer i1 = atomIdentifierMapping.get(edge.getSource().getAtomIdentifier());
                if (i1 == null) continue;
                Integer i2 = atomIdentifierMapping.get(edge.getTarget().getAtomIdentifier());
                if (i2 == null) continue;
                Optional<PdbAtom> atom1 = renumberedLeafSubstructure.getAtom(i1);
                Optional<PdbAtom> atom2 = renumberedLeafSubstructure.getAtom(i2);
                if (!atom1.isPresent() || !atom2.isPresent()) continue;

                PdbBond edgeCopy = new PdbBond(edge.getIdentifier(), edge.getBondType());
                renumberedLeafSubstructure.addBondBetween(edgeCopy, atom1.get(), atom2.get());
            }
        }
        renumberedLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
        return renumberedLeafSubstructure;

    }


    private PdbStructure renumberLinkEntries(PdbStructure structure) {
        if (structure.getLinkEntries() == null) {
            return structure;
        }
        for (LinkEntry linkEntry : structure.getLinkEntries()) {
            Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> firstAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getFirstAtom().getAtomIdentifier()));
            Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> secondAtomEntry = structure.getUniqueAtomEntry(atomIdentifierMapping.get(linkEntry.getSecondAtom().getAtomIdentifier()));
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

    private PdbStructure renumberLeafSubstructures(PdbStructure structure, Map<AuthLeafIdentifier, Integer> renumberingMap) {
        PdbStructure renumberedStructure = new PdbStructure();
        renumberedStructure.setPdbIdentifier(structure.getStructureIdentifier());
        for (Model model : structure.getAllModels()) {
            PdbModel renumberedModel = new PdbModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                PdbChain pdbChain = (PdbChain) chain;
                PdbChain renumberedChain = new PdbChain(chain.getChainIdentifier());
                renumberedModel.addChain(renumberedChain);
                for (LeafSubstructure leafSubstructure : pdbChain.getConsecutivePart()) {
                    AuthLeafIdentifier originalIdentifier = ((AuthLeafIdentifier) leafSubstructure.getIdentifier());
                    // skip leaves not specified in the renumbering map
                    if (!renumberingMap.containsKey(originalIdentifier)) {
                        continue;
                    }
                    AuthLeafIdentifier renumberedIdentifier = new AuthLeafIdentifier(
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
                PdbChain renumberedChain = renumberedModel.getChain(chain.getChainIdentifier()).orElseThrow(NoSuchElementException::new);
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
