package bio.singa.structure.io.general;

import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.*;
import bio.singa.structure.io.pdb.tokens.*;

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
    private List<String> consecutiveRecords;

    /**
     * The terminating record of any string.
     */
    private String terminateRecord;

    /**
     * The non-consecutive part of any pdb file.
     */
    private List<? extends LeafSubstructure> nonConsecutiveLeafs;

    /**
     * Creates a representation of the given chain. For multiple chains, use the {@link Model} to encapsulate them.
     *
     * @param chain The chain.
     */
    StructureRepresentation(Chain chain) {
        PdbChain pdbChain = (PdbChain) chain;
        List<PdbLeafSubstructure> consecutivePart = pdbChain.getConsecutivePart();
        consecutiveRecords = getPdbLines(consecutivePart);
        terminateRecord = consecutivePart.isEmpty() ? "" : ChainTerminatorToken.assemblePDBLine(consecutivePart.get(consecutivePart.size() - 1));
        nonConsecutiveLeafs = pdbChain.getNonConsecutivePart();
    }

    /**
     * Returns a list of pdb lines from any collection of leaves.
     *
     * @param leafSubstructures The laves to convertToSpheres.
     * @return A list of atom lines.
     */
    public List<String> getPdbLines(Collection<? extends LeafSubstructure> leafSubstructures) {
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
    public String getConsecutiveRepresentation() {
        return consecutiveRecords.stream()
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    public String getTerminateRecord() {
        return terminateRecord + System.lineSeparator();
    }

    public List<String> getConsecutiveRecords() {
        return consecutiveRecords;
    }

    public List<? extends LeafSubstructure> getNonConsecutiveLeafs() {
        return nonConsecutiveLeafs;
    }
}
