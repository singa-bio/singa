package de.bioforscher.singa.sequence.parser.ena;

import de.bioforscher.singa.features.identifiers.ENAAccessionNumber;
import de.bioforscher.singa.sequence.model.NucleotideSequence;
import de.bioforscher.singa.sequence.model.ProteinSequence;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * @author cl
 */
public class ENAContentHandler implements ContentHandler {

    private String currentTag = "";
    private boolean isInTranslation;
    private boolean isInTranslationTable;
    private StringBuilder translationSequenceBuilder;
    private StringBuilder sequenceBuilder;
    private int translationTable;

    private ENAAccessionNumber enaAccessionNumber;

    public ENAContentHandler(ENAAccessionNumber enaAccessionNumber) {
        this.enaAccessionNumber = enaAccessionNumber;
        translationSequenceBuilder = new StringBuilder();
        sequenceBuilder = new StringBuilder();
    }

    public NucleotideSequence getNucleotideSequence() {
        final String sequence = sequenceBuilder.toString().replaceAll("\\s", "");
        NucleotideSequence nucleotideSequence = NucleotideSequence.of(sequence);
        nucleotideSequence.setFeature(enaAccessionNumber);
        return nucleotideSequence;
    }

    public ProteinSequence getTranslationSequence() {
        final String translationSequence = translationSequenceBuilder.toString().replaceAll("\\s", "");
        return ProteinSequence.of(translationSequence);

    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() {

    }

    @Override
    public void endDocument() {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {

    }

    @Override
    public void endPrefixMapping(String prefix) {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        switch (qName) {
            case "value":
            case "sequence": {
                currentTag = qName;
                break;
            }
            case "qualifier": {
                final String name = atts.getValue("name");
                if (name.equals("translation")) {
                    isInTranslation = true;
                }
                if (name.equals("transl_table")) {
                    isInTranslationTable = true;
                }
                break;
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "sequence": {
                currentTag = "";
                break;
            }
            case "qualifier": {
                isInTranslation = false;
                isInTranslationTable = false;
                break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        switch (currentTag) {
            case "value": {
                if (isInTranslation) {
                    translationSequenceBuilder.append(new String(ch, start, length));
                }
                if (isInTranslationTable) {
                    final String string = new String(ch, start, length);
                    if (string.matches("\\s*\\d+\\s*")) {
                        translationTable = Integer.valueOf(string.trim());
                    }
                }
                break;
            }
            case "sequence": {
                sequenceBuilder.append(new String(ch, start, length));
            }
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {

    }

    @Override
    public void processingInstruction(String target, String data) {

    }

    @Override
    public void skippedEntity(String name) {

    }
}
