package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.sequence.model.interfaces.Sequence;

import java.util.HashMap;
import java.util.Map;

/**
 * A sequence container encapsulates biological sequences that are connected on some level.
 * Such as nucleotide sequences and their translation or RNA transcripts with intron and exon segments.
 *
 * @author cl
 */
public class SequenceContainer {

    public static String GENE = "GENE";
    public static String RNA_TRANSCRIPT = "RNA_TRANSCRIPT";
    public static String TRANSLATION = "TRANSLATION";

    private Map<String, Sequence<?>> container;

    public SequenceContainer() {
        container = new HashMap<>();
    }

    public void addSequence(String identifyingDescription, Sequence<?> sequence) {
        container.put(identifyingDescription, sequence);
    }

    public Sequence<?> getSequence(String identifyingDescription) {
        return container.get(identifyingDescription);
    }

    public NucleotideSequence getGene() {
        return (NucleotideSequence) getSequence(GENE);
    }

    public ProteinSequence getTranslation() {
        return (ProteinSequence) getSequence(TRANSLATION);
    }


}
