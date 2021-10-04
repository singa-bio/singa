package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.AtomContainer;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.*;
import bio.singa.structure.parser.pdb.structures.tokens.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents any {@link AtomContainer} in a pdb format, ready to be written to a file.
 *
 * @author sb
 */
public class StructureRepresentation {

    /**
     * The consecutive part of the pdb file.
     */
    private final List<String> consecutiveRecords;

    /**
     * The terminating record of any string.
     */
    private final String terminateRecord;

    /**
     * The non-consecutive part of any pdb file.
     */
    private final List<PdbLeafSubstructure> nonConsecutiveLeafs;

    /**
     * Creates a representation of the given chain. For multiple chains, use the {@link Model} to encapsulate them.
     *
     * @param chain The chain.
     */
    private StructureRepresentation(PdbChain chain) {
        List<PdbLeafSubstructure> consecutivePart = chain.getConsecutivePart();
        consecutiveRecords = getPdbLines(consecutivePart);
        terminateRecord = consecutivePart.isEmpty() ? "" : ChainTerminatorToken.assemblePDBLine(consecutivePart.get(consecutivePart.size()));
        nonConsecutiveLeafs = chain.getNonConsecutivePart();
    }

    /**
     * Creates a pdb representation of the given structure.
     *
     * @param structure The structure.
     * @return The string representing the structure in pdb format.
     */
    public static String composePdbRepresentation(Structure structure) {
        StringBuilder sb = new StringBuilder();
        // add preamble
        sb.append(getPreamble(structure.getStructureIdentifier(), structure.getTitle(), ((PdbStructure) structure).getLinkEntries()));
        // get all models
        List<PdbModel> allModels = structure.getAllModels().stream()
                .map(PdbModel.class::cast)
                .collect(Collectors.toList());
        // if there is only one model
        if (allModels.size() == 1) {
            // get it
            PdbModel structuralModel = allModels.iterator().next();
            appendChainRepresentations(sb, structuralModel);
        } else {
            for (PdbModel model : allModels) {
                sb.append("MODEL ").append(String.format("%5d", model.getModelIdentifier())).append(System.lineSeparator());
                appendChainRepresentations(sb, model);
                sb.append("ENDMDL").append(System.lineSeparator());
            }
        }
        // add postamble
        sb.append(getPostamble(structure.getAllLeafSubstructures()));
        return sb.toString();
    }

    /**
     * Adds all chains in the model to the given string builder.
     *
     * @param sb The string builder to append to.
     * @param structuralModel The model to be appended.
     */
    private static void appendChainRepresentations(StringBuilder sb, PdbModel structuralModel) {
        // create chain representations
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains().stream()
                .map(PdbChain.class::cast)
                .filter(oakChain -> !oakChain.getAllLeafSubstructures().isEmpty())
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());
        // collect nonconsecutive records for all chains and append consecutive parts to builder
        List<LeafSubstructure> nonConsecutiveRecords = new ArrayList<>();
        for (StructureRepresentation chainRepresentation : chainRepresentations) {
            if (!chainRepresentation.consecutiveRecords.isEmpty()) {
                sb.append(chainRepresentation.getConsecutiveRepresentation())
                        .append(chainRepresentation.getTerminateRecord());
            }
            nonConsecutiveRecords.addAll(chainRepresentation.getNonConsecutiveLeafSubstructures());
        }
        // append non non consecutive part
        sb.append(composePdbRepresentationOfNonConsecutiveRecords(nonConsecutiveRecords));
    }

    /**
     * Creates a representation of the given model.
     *
     * @param structuralModel The model.
     * @return A string representing the information of the structure in pdb format.
     */
    public static String composePdbRepresentation(PdbModel structuralModel) {
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains().stream()
                .map(PdbChain.class::cast)
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        List<LeafSubstructure> nonConsecutiveRecords = new ArrayList<>();
        for (StructureRepresentation chainRepresentation : chainRepresentations) {
            stringBuilder.append(chainRepresentation.getConsecutiveRepresentation())
                    .append(chainRepresentation.getTerminateRecord());
            nonConsecutiveRecords.addAll(chainRepresentation.getNonConsecutiveLeafSubstructures());
        }

        stringBuilder.append(composePdbRepresentationOfNonConsecutiveRecords(nonConsecutiveRecords));

        return stringBuilder.toString();
    }

    /**
     * Composes the pdb lines for each leaf to a single string.
     *
     * @param nonConsecutiveLeafs The leaf substructures to be written.
     * @return A string representing the information of the leaves in pdb format.
     */
    private static String composePdbRepresentationOfNonConsecutiveRecords(List<LeafSubstructure> nonConsecutiveLeafs) {
        // sorts the leafy by their atom identifier
        if (!nonConsecutiveLeafs.isEmpty()) {
            nonConsecutiveLeafs.sort(Comparator.comparingInt(nonConsecutiveLeaf -> nonConsecutiveLeaf.getAllAtoms().iterator().next().getAtomIdentifier()));
            return nonConsecutiveLeafs.stream()
                    .map(AtomToken::assemblePDBLine)
                    .flatMap(Collection::stream)
                    .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
        }
        return "";
    }

    /**
     * The title and header line for this structure.
     *
     * @return The title and header line for this structure.
     */
    private static String getPreamble(String pdbIdentifier, String title, List<PdbLinkEntry> linkEntries) {
        StringBuilder sb = new StringBuilder();
        if (pdbIdentifier != null && !pdbIdentifier.equals(PdbLeafIdentifier.DEFAULT_PDB_IDENTIFIER)) {
            sb.append(HeaderToken.assemblePDBLine(pdbIdentifier));
            sb.append(System.lineSeparator());
        }
        if (title != null && !title.isEmpty()) {
            for (String titleLine : TitleToken.assemblePDBLines(title)) {
                sb.append(titleLine);
                sb.append(System.lineSeparator());
            }
        }
        for (PdbLinkEntry linkEntry : linkEntries) {
            sb.append(LinkToken.assemblePDBLine(linkEntry));
        }
        return sb.toString();
    }

    /**
     * The closing lines.
     *
     * @return The closing lines.
     */
    private static String getPostamble(Collection<? extends LeafSubstructure> leafSubstructures) {
        String connectRecords = leafSubstructures.stream()
                .filter(leafSubstructure -> leafSubstructure.getClass().equals(PdbLigand.class))
                .map(PdbLigand.class::cast)
                .map(ConnectionToken::assemblePDBLines)
                .collect(Collectors.joining());
        return connectRecords + "END" + System.lineSeparator() + System.lineSeparator();
    }

    /**
     * Returns a list of pdb lines from any collection of leaves.
     *
     * @param leafSubstructures The laves to convertToSpheres.
     * @return A list of atom lines.
     */
    private List<String> getPdbLines(Collection<PdbLeafSubstructure> leafSubstructures) {
        return leafSubstructures.stream()
                .map(AtomToken::assemblePDBLine)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns the string representing the consecutive part of this structural representation.
     *
     * @return The string representing the consecutive part of this structural representation.
     */
    private String getConsecutiveRepresentation() {
        return consecutiveRecords.stream()
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    /**
     * Returns the terminating record for this representation.
     *
     * @return The terminating record for this representation.
     */
    private String getTerminateRecord() {
        return terminateRecord + System.lineSeparator();
    }

    /**
     * Returns the actual leaves of the nonconsecutive part.
     *
     * @return The actual leaves of the nonconsecutive part.
     */
    private List<PdbLeafSubstructure> getNonConsecutiveLeafSubstructures() {
        return nonConsecutiveLeafs;
    }
}
