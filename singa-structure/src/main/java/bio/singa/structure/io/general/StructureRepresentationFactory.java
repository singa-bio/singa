package bio.singa.structure.io.general;

import bio.singa.structure.io.pdb.tokens.*;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StructureRepresentationFactory {

    private StructureRepresentationOptions options;
    private Structure structure;

    public StructureRepresentationFactory(StructureRepresentationOptions options) {
        this.options = options;
    }

    public static String getPdbStringRepresentation(Structure structure, StructureRepresentationOptions options) {
        StructureRepresentationFactory factory = new StructureRepresentationFactory(options);
        return factory.composePdbRepresentation(structure);
    }

    /**
     * Creates a pdb representation of the given structure.
     *
     * @param structure The structure.
     * @return The string representing the structure in pdb format.
     */
    public String composePdbRepresentation(Structure structure) {
        this.structure = structure;
        StringBuilder sb = new StringBuilder();
        // add preamble
        sb.append(getPreamble(structure.getStructureIdentifier(), structure.getTitle(), determineLinkEntries(structure)));
        // get all models
        List<Integer> modelIdentifiers = structure.getAllModels().stream()
                .map(Model::getModelIdentifier)
                .collect(Collectors.toList());
        // if there is only one model
        if (modelIdentifiers.size() == 1) {
            // get it
            Model structuralModel = structure.getFirstModel();
            appendChainRepresentations(sb, structuralModel);
        } else {
            for (Integer modelIdentifier : modelIdentifiers) {
                Optional<? extends Model> optionalModel = structure.getModel(modelIdentifier);
                if (!optionalModel.isPresent()) {
                    continue;
                }
                Model model = optionalModel.get();
                sb.append("MODEL ").append(String.format("%5d", model.getModelIdentifier())).append(System.lineSeparator());
                appendChainRepresentations(sb, model);
                sb.append("ENDMDL").append(System.lineSeparator());
            }
        }
        // add postamble
        sb.append(getPostamble(structure.getAllLeafSubstructures()));
        return sb.toString();
    }

    private List<PdbLinkEntry> determineLinkEntries(Structure structure) {
        if (structure instanceof PdbStructure) {
            return ((PdbStructure) structure).getLinkEntries();
        }
        return Collections.emptyList();
    }

    /**
     * Adds all chains in the model to the given string builder.
     *
     * @param sb The string builder to append to.
     * @param structuralModel The model to be appended.
     */
    private void appendChainRepresentations(StringBuilder sb, Model structuralModel) {
        // create chain representations
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains().stream()
                .filter(chain -> !chain.getAllLeafSubstructures().isEmpty())
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());
        // collect nonconsecutive records for all chains and append consecutive parts to builder
        List<LeafSubstructure> nonConsecutiveRecords = new ArrayList<>();
        for (StructureRepresentation chainRepresentation : chainRepresentations) {
            if (!chainRepresentation.getConsecutiveRecords().isEmpty()) {
                sb.append(chainRepresentation.getConsecutiveRepresentation())
                        .append(chainRepresentation.getTerminateRecord());
            }
            nonConsecutiveRecords.addAll(chainRepresentation.getNonConsecutiveLeafs());
        }
        // append non non consecutive part
        sb.append(composePdbRepresentationOfNonConsecutiveRecords(nonConsecutiveRecords));
    }

    /**
     * Composes the pdb lines for each leaf to a single string.
     *
     * @param nonConsecutiveLeafs The leaf substructures to be written.
     * @return A string representing the information of the leaves in pdb format.
     */
    private String composePdbRepresentationOfNonConsecutiveRecords(List<LeafSubstructure> nonConsecutiveLeafs) {
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
    private String getPreamble(String pdbIdentifier, String title, List<PdbLinkEntry> linkEntries) {
        StringBuilder sb = new StringBuilder();
        // header
        if (pdbIdentifier != null && !pdbIdentifier.equals(PdbLeafIdentifier.DEFAULT_PDB_IDENTIFIER)) {
            sb.append(HeaderToken.assemblePDBLine(pdbIdentifier));
            sb.append(System.lineSeparator());
        }
        // title
        if (title != null && !title.isEmpty()) {
            for (String titleLine : TitleToken.assemblePDBLines(title)) {
                sb.append(titleLine);
                sb.append(System.lineSeparator());
            }
        }
        // remarks
        if (options.isAddRemark80()) {
            structure.getAllLigands().stream()
                    .map(PdbLigand.class::cast)
                    .filter(distinctByKey(PdbLeafSubstructure::getFamily))
                    .map(ligand -> Remark80Token.assemblePDBLines(ligand.getThreeLetterCode(), ligand.getInchi()))
                    .forEach(sb::append);
            sb.append("REMARK  80").append(System.lineSeparator());
        }
        // links
        for (PdbLinkEntry linkEntry : linkEntries) {
            sb.append(LinkToken.assemblePDBLine(linkEntry));
        }
        return sb.toString();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * The closing lines.
     *
     * @return The closing lines.
     */
    private String getPostamble(Collection<? extends LeafSubstructure> leafSubstructures) {
        String connectRecords = "";
        if (options.isAddConnections()) {
            connectRecords = leafSubstructures.stream()
                    .filter(leafSubstructure -> leafSubstructure.getClass().equals(PdbLigand.class))
                    .map(PdbLigand.class::cast)
                    .map(ConnectionToken::assemblePDBLines)
                    .collect(Collectors.joining());
        }
        return connectRecords + "END" + System.lineSeparator() + System.lineSeparator();
    }

}
