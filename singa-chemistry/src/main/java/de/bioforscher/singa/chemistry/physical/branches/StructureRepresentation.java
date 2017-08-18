package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StructureRepresentation {

    private final List<String> consecutiveRecords;
    private final String terminateRecord;
    private final List<LeafSubstructure<?, ?>> nonConsecutiveLeafs;

    private StructureRepresentation(Chain chain) {
        List<LeafSubstructure<?, ?>> consecutivePart = chain.getConsecutivePart();
        this.consecutiveRecords = getPdbLines(consecutivePart);
        this.terminateRecord = composeTerminateRecord(consecutivePart.get(consecutivePart.size() - 1));
        this.nonConsecutiveLeafs = chain.getNonConsecutivePart();
    }

    public static String composePdbRepresentation(Chain chain) {
        StructureRepresentation structureRepresentation = new StructureRepresentation(chain);
        return structureRepresentation.getConsecutiveRepresentation() +
                structureRepresentation.getTerminateRecord() +
                composePdbRepresentationOfNonConsecutiveRecords(structureRepresentation.getNonConsecutiveLeafs());
    }

    public static String composePdbRepresentation(StructuralModel structuralModel) {
        List<StructureRepresentation> chainRepresentations = structuralModel.getAllChains()
                .stream()
                .map(StructureRepresentation::new)
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        List<LeafSubstructure<?, ?>> nonConsecutiveRecords = new ArrayList<>();
        for (StructureRepresentation chainRepresentation : chainRepresentations) {
            stringBuilder.append(chainRepresentation.getConsecutiveRepresentation())
                    .append(chainRepresentation.getTerminateRecord());
            nonConsecutiveRecords.addAll(chainRepresentation.getNonConsecutiveLeafs());
        }

        stringBuilder.append(composePdbRepresentationOfNonConsecutiveRecords(nonConsecutiveRecords));

        return stringBuilder.toString();
    }

    private static String composePdbRepresentationOfNonConsecutiveRecords(List<LeafSubstructure<?, ?>> nonConsecutiveLeafs) {
        nonConsecutiveLeafs.sort(Comparator.comparingInt(nonConsecutiveLeaf -> nonConsecutiveLeaf.getAllAtoms().get(0).getIdentifier()));

        return nonConsecutiveLeafs.stream()
                .map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    private String composeTerminateRecord(LeafSubstructure<?, ?> leafSubstructure) {
        // TER     961      ASP A  62
        return "TER   " +
                String.format("%5d", (leafSubstructure.getAllAtoms().get(leafSubstructure.getAllAtoms().size() - 1).getIdentifier() + 1)) +
                "      " +
                leafSubstructure.getFamily().getThreeLetterCode().toUpperCase() +
                " " +
                leafSubstructure.getChainIdentifier() +
                String.format("%4d", leafSubstructure.getIdentifier().getSerial()) +
                leafSubstructure.getIdentifier().getInsertionCode();
    }

    private List<String> getPdbLines(Collection<LeafSubstructure<?, ?>> leafSubstructures) {
        return leafSubstructures
                .stream()
                .map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String getConsecutiveRepresentation() {
        return consecutiveRecords.stream()
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    private String getTerminateRecord() {
        return terminateRecord + System.lineSeparator();
    }

    public List<LeafSubstructure<?, ?>> getNonConsecutiveLeafs() {
        return nonConsecutiveLeafs;
    }
}
