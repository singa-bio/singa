package de.bioforscher.singa.core.biology;

/**
 * @author cl
 */
public class NucleotideSequence {

    private String sequence;
    private String translationSequence;
    private int translationTable;

    public NucleotideSequence(String sequence, String translationSequence, int translationTable) {
        this.sequence = sequence;
        this.translationSequence = translationSequence;
        this.translationTable = translationTable;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getTranslationSequence() {
        return translationSequence;
    }

    public void setTranslationSequence(String translationSequence) {
        this.translationSequence = translationSequence;
    }

    public int getTranslationTable() {
        return translationTable;
    }

    public void setTranslationTable(int translationTable) {
        this.translationTable = translationTable;
    }
}
