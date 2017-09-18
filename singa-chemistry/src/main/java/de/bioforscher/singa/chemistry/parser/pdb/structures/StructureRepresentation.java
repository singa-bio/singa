package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.ChainTerminatorToken;
import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.HeaderToken;
import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.TitleToken;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import de.bioforscher.singa.chemistry.physical.model.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents any {@link StructuralEntity} in a pdb format, ready to be written to a file.
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
    private final List<LeafSubstructure<?, ?>> nonConsecutiveLeafs;

    /**
     * Creates a representation of the given chain. For multiple chains, use the {@link StructuralModel} to encapsulate
     * them.
     *
     * @param chain The chain.
     */
    private StructureRepresentation(Chain chain) {
        List<LeafSubstructure<?, ?>> consecutivePart = chain.getConsecutivePart();
        this.consecutiveRecords = getPdbLines(consecutivePart);
        this.terminateRecord = ChainTerminatorToken.assemblePDBLine(consecutivePart.get(consecutivePart.size() - 1));
        this.nonConsecutiveLeafs = chain.getNonConsecutivePart();
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
        sb.append(getPreamble(structure.getPdbIdentifier(), structure.getTitle()));
        // get all models
        List<StructuralModel> allModels = structure.getAllModels();
        // if there is only one model
        if (allModels.size() == 1) {
            // get it
            StructuralModel structuralModel = allModels.iterator().next();
            appendChainRepresentations(sb, structuralModel);
        } else {
            for (StructuralModel model : allModels) {
                sb.append("MODEL ").append(String.format("%5d", model.getIdentifier())).append(System.lineSeparator());
                appendChainRepresentations(sb, model);
                sb.append("ENDMDL").append(System.lineSeparator());
            }
        }
        // add postamble
        sb.append(getPostamble());
        return sb.toString();
    }

    /**
     * Creates a pdb representation of the given structure.
     * @param leaves The leaves.
     * @return The string representing the structure in pdb format.
     */
    public static String composePdbRepresentation(List<LeafSubstructure<?, ?>> leaves) {
        StringBuilder sb = new StringBuilder();
        LeafSubstructure<?, ?> first = leaves.iterator().next();
        // add preamble
        sb.append(getPreamble(first.getPdbIdentifier(), ""));
        // if there is only one model
        sb.append(composePdbRepresentationOfNonConsecutiveRecords(leaves));
        // add postamble
        sb.append(getPostamble());
        return sb.toString();
    }

    /**
     * Adds all chains in the model to the given string builder.
     * @param sb The string builder to append to.
     * @param structuralModel The model to be appended.
     */
    private static void appendChainRepresentations(StringBuilder sb, StructuralModel structuralModel) {
        // create chain representations
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains().stream()
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());
        // collect nonconsecutive records for all chains and append consecutive parts to builder
        List<LeafSubstructure<?, ?>> nonConsecutiveRecords = new ArrayList<>();
        for (StructureRepresentation chainRepresentation : chainRepresentations) {
            sb.append(chainRepresentation.getConsecutiveRepresentation())
                    .append(chainRepresentation.getTerminateRecord());
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
    public static String composePdbRepresentation(StructuralModel structuralModel) {
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains().stream()
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        List<LeafSubstructure<?, ?>> nonConsecutiveRecords = new ArrayList<>();
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
    private static String composePdbRepresentationOfNonConsecutiveRecords(List<LeafSubstructure<?, ?>> nonConsecutiveLeafs) {
        // sorts the leafy by their atom identifier
        if (!nonConsecutiveLeafs.isEmpty()) {
            nonConsecutiveLeafs.sort(Comparator.comparingInt(nonConsecutiveLeaf -> nonConsecutiveLeaf.getAllAtoms().get(0).getIdentifier()));
            return nonConsecutiveLeafs.stream()
                    .map(LeafSubstructure::getPdbLines)
                    .flatMap(Collection::stream)
                    .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
        }
        return "";
    }

    /**
     * Returns a list of pdb lines from any collection of leaves.
     *
     * @param leafSubstructures The laves to convert.
     * @return A list of atom lines.
     */
    private List<String> getPdbLines(Collection<LeafSubstructure<?, ?>> leafSubstructures) {
        return leafSubstructures.stream()
                .map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns the string representing the consecutive part of this structural representation.
     *
     * @return The string representing the consecutive part of this structural representation.
     */
    private String getConsecutiveRepresentation() {
        return this.consecutiveRecords.stream()
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    /**
     * Returns the terminating record for this representation.
     *
     * @return The terminating record for this representation.
     */
    private String getTerminateRecord() {
        return this.terminateRecord + System.lineSeparator();
    }

    /**
     * Returns the actual leaves of the nonconsecutive part.
     *
     * @return The actual leaves of the nonconsecutive part.
     */
    private List<LeafSubstructure<?, ?>> getNonConsecutiveLeafSubstructures() {
        return this.nonConsecutiveLeafs;
    }

    /**
     * The title and header line for this structure.
     *
     * @return The title and header line for this structure.
     */
    private static String getPreamble(String pdbIdentifier, String title) {
        StringBuilder sb = new StringBuilder();
        if (pdbIdentifier != null && !pdbIdentifier.equals(LeafIdentifier.DEFAULT_PDB_IDENTIFIER)) {
            sb.append(HeaderToken.assemblePDBLine(pdbIdentifier));
            sb.append(System.lineSeparator());
        }
        if (title != null && !title.isEmpty()) {
            for (String titleLine : TitleToken.assemblePDBLines(title)) {
                sb.append(titleLine);
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * The closing lines.
     *
     * @return The closing lines.
     */
    private static String getPostamble() {
        return "END" + System.lineSeparator() + System.lineSeparator();
    }
}
