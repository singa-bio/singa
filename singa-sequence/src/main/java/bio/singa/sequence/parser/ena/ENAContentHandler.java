package bio.singa.sequence.parser.ena;

import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.sequence.model.NucleotideSequence;
import bio.singa.sequence.model.ProteinSequence;
import bio.singa.sequence.model.interfaces.Sequence;
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
    private StringBuilder geneSequenceBuilder;
    private int translationTable;

    private ENAAccessionNumber enaAccessionNumber;
    private UniProtIdentifier uniProtIdentifier;

    public ENAContentHandler(ENAAccessionNumber enaAccessionNumber) {
        this.enaAccessionNumber = enaAccessionNumber;
        translationSequenceBuilder = new StringBuilder();
        geneSequenceBuilder = new StringBuilder();
    }

    public NucleotideSequence getNucleotideSequence() {
        final String sequence = geneSequenceBuilder.toString().replaceAll("\\s", "");
        NucleotideSequence nucleotideSequence = new NucleotideSequence(sequence);
        nucleotideSequence.setFeature(enaAccessionNumber);
        return nucleotideSequence;
    }

    public ProteinSequence getTranslationSequence() {
        final String translationSequence = translationSequenceBuilder.toString().replaceAll("\\s", "");
        ProteinSequence proteinSequence = new ProteinSequence(translationSequence);
        if (uniProtIdentifier != null) {
            proteinSequence.setFeature(uniProtIdentifier);
        }
        return proteinSequence;
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
            case "xref": {
                final String db = atts.getValue("db");
                if (db.equals("UniProtKB/Swiss-Prot")) {
                    uniProtIdentifier = new UniProtIdentifier(atts.getValue("id"));
                }
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
                geneSequenceBuilder.append(new String(ch, start, length));
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
