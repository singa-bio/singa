package de.bioforscher.chemistry.parser;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.core.biology.Organism;
import de.bioforscher.core.biology.Taxon;
import de.bioforscher.core.identifier.NCBITaxonomyIdentifier;
import de.bioforscher.core.identifier.UniProtIdentifier;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 10.09.2016.
 */
public class UniProtContentHandler implements ContentHandler {

    // chemical entity attributes
    private UniProtIdentifier identifier;
    private String recommendedName;
    private double molarMass;
    private List<String> additionalNames;
    private Organism sourceOrganism;

    // parser attributes
    private String currentTag = "";

    // reading name
    private boolean inRecommendedName = false;
    private boolean inAlternativeName = false;
    private boolean inOrganism = false;
    private boolean isScientificName = false;

    public UniProtContentHandler() {
        this.additionalNames = new ArrayList<>();
    }

    Enzyme getChemicalSpecies() {
        Enzyme enzyme = new Enzyme.Builder(this.identifier)
                .name(this.recommendedName)
                .molarMass(this.molarMass)
                .build();
        enzyme.setSourceOrganism(this.sourceOrganism);
        this.additionalNames.forEach(enzyme::addAdditionalName);

        return enzyme;
    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        switch (qName) {
            case "accession":
            case "fullName":
            case "taxon":{
                this.currentTag = qName;
                break;
            }
            case "recommendedName": {
                this.currentTag = qName;
                this.inRecommendedName = true;
                break;
            }
            case "alternativeName": {
                this.currentTag = qName;
                this.inAlternativeName = true;
                break;
            }
            case "organism": {
                this.currentTag = qName;
                this.inOrganism = true;
                break;
            }
            case "name": {
                this.currentTag = qName;
                if (this.inOrganism && atts.getValue("type").equals("scientific")) {
                    this.isScientificName = true;
                }
                break;
            }
            case "dbReference": {
                if (this.inOrganism && atts.getValue("type").equals("NCBI Taxonomy")) {
                    // set tax id for organism
                    this.sourceOrganism.setIdentifier(new NCBITaxonomyIdentifier(atts.getValue("id")));
                }
                break;
            }
            case "sequence": {
                // set weight
                this.molarMass = Double.valueOf(atts.getValue("mass"));
                break;
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals(this.currentTag)) {
            this.currentTag = "";
        }

        switch (qName) {
            case "recommendedName": {
                this.inRecommendedName = false;
                break;
            }
            case "alternativeName": {
                this.inAlternativeName = false;
                break;
            }
            case "organism": {
                this.inOrganism = false;
                break;
            }
            case "name": {
                this.isScientificName = false;
                break;
            }
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        switch (this.currentTag) {
            case "accession": {
                if (this.identifier == null) {
                    // set identifier
                    this.identifier = new UniProtIdentifier(new String(ch, start, length));
                }
                break;
            }
            case "fullName": {
                if (this.inRecommendedName) {
                    // set recommended name
                    this.recommendedName = new String(ch, start, length);
                } else if (this.inAlternativeName) {
                    // add alternative name
                    this.additionalNames.add(new String(ch, start, length));
                }
                break;
            }
            case "name": {
                if (this.inOrganism && this.isScientificName) {
                    // create Organism with name
                    this.sourceOrganism = new Organism(new String(ch, start, length));
                }
                break;
            }
            case "taxon": {
                if (this.inOrganism) {
                    // add linage to organism
                    this.sourceOrganism.getLineage().add(new Taxon(new String(ch, start, length)));
                }
                break;
            }

        }

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

}
