package de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot;

import de.bioforscher.singa.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.singa.chemistry.descriptive.annotations.AnnotationType;
import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.core.biology.Organism;
import de.bioforscher.singa.core.biology.Taxon;
import de.bioforscher.singa.core.identifier.ECNumber;
import de.bioforscher.singa.core.identifier.NCBITaxonomyIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class UniProtContentHandler implements ContentHandler {

    private static final List<String> TEXT_COMMENTS_TO_PARSE = new ArrayList<>();

    static {
        Collections.addAll(TEXT_COMMENTS_TO_PARSE,
                "function",
                "catalytic activity");
    }

    // preassigned primary Identifier
    private String primaryIdentifier;

    // enzyme attributes
    private UniProtIdentifier identifier;
    private String recommendedName;
    private double molarMass;
    private List<String> additionalNames;
    private String aminoAcidSequence;
    private Organism sourceOrganism;
    private List<Annotation<String>> textComments;
    private List<ECNumber> ecNumbers;

    // parser attributes
    private String currentTag = "";
    private Annotation<String> temoraryCommentAnnotation;

    // reading name
    private boolean inRecommendedName = false;
    private boolean inAlternativeName = false;
    private boolean inOrganism = false;
    private boolean inRelevantComment = false;
    private boolean isScientificName = false;
    private boolean isCommonName = false;

    public UniProtContentHandler() {
        additionalNames = new ArrayList<>();
        textComments = new ArrayList<>();
        ecNumbers = new ArrayList<>();
    }

    public UniProtContentHandler(String primaryIdentifier) {
        this();
        this.primaryIdentifier = primaryIdentifier;
    }

    Protein getProtein() {
        // create base enzyme
        Protein protein;
        if (primaryIdentifier == null) {
            protein = new Protein.Builder(identifier.toString())
                    .name(recommendedName)
                    .assignFeature(new MolarMass(molarMass, UniProtDatabase.origin))
                    .build();
        } else {
            protein = new Enzyme.Builder(primaryIdentifier)
                    .additionalIdentifier(identifier)
                    .name(recommendedName)
                    .assignFeature(new MolarMass(molarMass, UniProtDatabase.origin))
                    .build();
        }
        // add organism
        protein.addOrganism(sourceOrganism);
        // add sequence without white spaces
        protein.addAminoAcidSequence(aminoAcidSequence.replaceAll("\\s", ""));
        // add additional names
        additionalNames.forEach(protein::addAdditionalName);
        // add textComments
        textComments.forEach(protein::addAnnotation);
        // add ecNumbers
        ecNumbers.forEach(protein::addAdditionalIdentifier);

        return protein;
    }

    Quantity<MolarMass> getMass() {
        return Quantities.getQuantity(molarMass, MolarMass.GRAM_PER_MOLE);
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
            case "accession":
            case "fullName":
            case "text":
            case "ecNumber":
            case "taxon": {
                currentTag = qName;
                break;
            }
            case "recommendedName": {
                currentTag = qName;
                inRecommendedName = true;
                break;
            }
            case "alternativeName": {
                currentTag = qName;
                inAlternativeName = true;
                break;
            }
            case "organism": {
                currentTag = qName;
                inOrganism = true;
                break;
            }
            case "comment": {
                if (TEXT_COMMENTS_TO_PARSE.contains(atts.getValue("type"))) {
                    currentTag = qName;
                    inRelevantComment = true;
                    temoraryCommentAnnotation = new Annotation<>(AnnotationType.NOTE);
                    temoraryCommentAnnotation.setDescription(atts.getValue("type"));
                }
                break;
            }
            case "name": {
                currentTag = qName;
                if (inOrganism) {
                    if (atts.getValue("type").equals("scientific")) {
                        isScientificName = true;
                    } else if (atts.getValue("type").equals("common")) {
                        isCommonName = true;
                    }
                }
                break;
            }
            case "dbReference": {
                if (inOrganism && atts.getValue("type").equals("NCBI Taxonomy")) {
                    // set tax id for organism
                    sourceOrganism.setIdentifier(new NCBITaxonomyIdentifier(atts.getValue("id")));
                }
                break;
            }
            case "sequence": {
                currentTag = qName;
                // set weight
                if (atts.getValue("mass") != null) {
                    molarMass = Double.valueOf(atts.getValue("mass"));
                    break;
                }
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(currentTag)) {
            currentTag = "";
        }

        switch (qName) {
            case "recommendedName": {
                inRecommendedName = false;
                break;
            }
            case "alternativeName": {
                inAlternativeName = false;
                break;
            }
            case "organism": {
                inOrganism = false;
                break;
            }
            case "name": {
                isScientificName = false;
                isCommonName = false;
                break;
            }
            case "comment": {
                if (inRelevantComment) {
                    if (temoraryCommentAnnotation.getContent() != null &&
                            !temoraryCommentAnnotation.getContent().trim().isEmpty()) {
                        textComments.add(temoraryCommentAnnotation);
                    }
                    inRelevantComment = false;
                }
            }
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {

        switch (currentTag) {
            case "accession": {
                // set pdbIdentifier
                identifier = new UniProtIdentifier(new String(ch, start, length));
                break;
            }
            case "ecNumber": {
                // add ec number
                ecNumbers.add(new ECNumber(new String(ch, start, length)));
                break;
            }
            case "fullName": {
                if (inRecommendedName) {
                    // set recommended name
                    recommendedName = new String(ch, start, length);
                } else if (inAlternativeName) {
                    // add alternative name
                    additionalNames.add(new String(ch, start, length));
                }
                break;
            }
            case "name": {
                if (inOrganism) {
                    if (isScientificName) {
                        // create Organism with name
                        sourceOrganism = new Organism(new String(ch, start, length));
                    } else if (isCommonName) {
                        // set common name
                        sourceOrganism.setCommonName(new String(ch, start, length));
                    }
                }
                break;
            }
            case "taxon": {
                if (inOrganism) {
                    // add linage to organism
                    sourceOrganism.getLineage().add(new Taxon(new String(ch, start, length)));
                }
                break;
            }
            case "sequence": {
                // set sequence
                if (aminoAcidSequence == null) {
                    aminoAcidSequence = new String(ch, start, length);
                } else {
                    aminoAcidSequence += new String(ch, start, length);
                }
                break;
            }
            case "text": {
                if (inRelevantComment) {
                    if (temoraryCommentAnnotation.getContent() == null) {
                        temoraryCommentAnnotation.setContent(new String(ch, start, length));
                    } else {
                        temoraryCommentAnnotation.setContent(temoraryCommentAnnotation.getContent()
                                + new String(ch, start, length));
                    }
                }
                break;
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
