package de.bioforscher.singa.core.biology;

import de.bioforscher.singa.core.identifier.ENAAccessionNumber;
import de.bioforscher.singa.core.identifier.model.Identifiable;

/**
 * @author cl
 */
public class NucleotideSequence implements Identifiable<ENAAccessionNumber> {

    private ENAAccessionNumber enaAccessionNumber;
    private String sequence;
    private String translationSequence;
    private int translationTable;

    public NucleotideSequence(ENAAccessionNumber enaAccessionNumber, String sequence, String translationSequence, int translationTable) {
        this.enaAccessionNumber = enaAccessionNumber;
        this.sequence = sequence;
        this.translationSequence = translationSequence;
        this.translationTable = translationTable;
    }

    @Override
    public ENAAccessionNumber getIdentifier() {
        return getEnaAccessionNumber();
    }

    public ENAAccessionNumber getEnaAccessionNumber() {
        return enaAccessionNumber;
    }

    public void setEnaAccessionNumber(ENAAccessionNumber enaAccessionNumber) {
        this.enaAccessionNumber = enaAccessionNumber;
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
