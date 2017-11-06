package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.InChIKey;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author cl
 */
public class ChEBIContentHandler implements ContentHandler {

    private String currentTag = "";

    private SimpleStringIdentifier identifier;
    private String name;
    private MolarMass molarMass;
    private StringBuilder smilesBuilder;
    private InChIKey inChIKey;

    private String primaryIdentifier;

    public ChEBIContentHandler() {
        this.smilesBuilder = new StringBuilder();
    }

    public ChEBIContentHandler(String primaryIdentifier) {
        this();
        this.primaryIdentifier = primaryIdentifier;
    }

    public Species getSpecies() {
        if (this.molarMass == null) {
            this.molarMass = new MolarMass(10.0, ChEBIDatabase.origin);
        }
        if (primaryIdentifier == null) {
            return new Species.Builder(this.identifier.toString())
                    .name(this.name)
                    .assignFeature(this.molarMass)
                    .assignFeature(new Smiles(smilesBuilder.toString(), ChEBIDatabase.origin))
                    .additionalIdentifier(inChIKey)
                    .build();
        } else {
            return new Species.Builder(this.primaryIdentifier)
                    .name(this.name)
                    .assignFeature(this.molarMass)
                    .assignFeature(new Smiles(smilesBuilder.toString(), ChEBIDatabase.origin))
                    .additionalIdentifier(new ChEBIIdentifier(identifier.toString()))
                    .additionalIdentifier(inChIKey)
                    .build();
        }
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
            case "chebiId":
            case "chebiAsciiName":
            case "mass":
            case "smiles":
            case "inchiKey":
                this.currentTag = qName;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (this.currentTag) {
            case "chebiId":
            case "chebiAsciiName":
            case "mass":
            case "smiles":
            case "inchiKey":
                this.currentTag = "";
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        switch (this.currentTag) {
            case "chebiId": {
                if (identifier == null) {
                    final String precursorString = new String(ch, start, length);
                    this.identifier = new SimpleStringIdentifier(precursorString);
                }
                break;
            }
            case "chebiAsciiName": {
                this.name = new String(ch, start, length);
                break;
            }
            case "mass": {
                final String precursorString = new String(ch, start, length);
                this.molarMass = new MolarMass(Double.valueOf(precursorString), ChEBIDatabase.origin);
                break;
            }
            case "smiles": {
                smilesBuilder.append(new String(ch, start, length));
                break;
            }
            case "inchiKey": {
                final String precursorString = new String(ch, start, length);
                this.inChIKey = new InChIKey(precursorString);
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
