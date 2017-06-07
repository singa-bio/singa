package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.singa.chemistry.descriptive.annotations.AnnotationType;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.PubChemIdentifier;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class PubChemContentHandler implements ContentHandler {

    // species attributes
    private SimpleStringIdentifier chebiIdentifier;
    private String pubChemIdentifier;
    private String name;
    private String smilesRepresentation;
    private double molarMass;
    private double logP;

    // parser attributes
    private String currentTag;

    // reading name
    private boolean inRecordTitle;
    private boolean inRecordTitleInformation;

    // reading SMILES
    private boolean inCanonicalSMILES;
    private boolean inCanonicalSMILESInformation;

    // reading molar mass
    private boolean inComputedProperties;
    private boolean inMolecularWeightInformation;

    // reading chebi identifier
    private boolean inSynonyms;
    private boolean inSynonymsInformation;

    // reading logP value
    private boolean inLogP;
    private boolean inLogPInformation;

    PubChemContentHandler() {
        this.currentTag = "";
    }

    public Species getSpecies() {

        Species result = new Species.Builder(this.chebiIdentifier)
                .name(this.name)
                .assignFeature(new MolarMass(this.molarMass, PubChemDatabase.origin))
                .assignFeature(new Smiles(this.smilesRepresentation, PubChemDatabase.origin))
                .assignFeature(new LogP(this.logP, PubChemDatabase.origin))
                .build();

        Annotation<PubChemIdentifier> pubChemIdentifierAnnotation = new Annotation<>(AnnotationType.ADDITIONAL_IDENTIFIER,
                new PubChemIdentifier("CID:"+this.pubChemIdentifier));
        result.addAnnotation(pubChemIdentifierAnnotation);

        return result;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        switch (this.currentTag) {
            case "RecordNumber": {
                // set pubchem identifier
                this.pubChemIdentifier = new String(ch, start, length);
                break;
            }
            case "TOCHeading": {
                String value = new String(ch, start, length);
                switch (value) {
                    case "Record Title":
                        this.inRecordTitle = true;
                        break;
                    case "Canonical SMILES":
                        this.inCanonicalSMILES = true;
                        break;
                    case "Computed Properties":
                        this.inComputedProperties = true;
                        break;
                    case "Depositor-Supplied Synonyms":
                        this.inSynonyms = true;
                        break;
                    case "LogP":
                        this.inLogP = true;
                        break;
                }
                break;
            }
            case "StringValue": {
                if (this.inRecordTitle && this.inRecordTitleInformation) {
                    // set name
                    this.name = new String(ch, start, length);
                    this.inRecordTitle = false;
                    this.inRecordTitleInformation = false;
                } else if (this.inCanonicalSMILES && this.inCanonicalSMILESInformation) {
                    // set smiles
                    this.smilesRepresentation = new String(ch, start, length);
                    this.inCanonicalSMILES = false;
                    this.inCanonicalSMILESInformation = false;
                } else if (this.inComputedProperties) {
                    // set logP
                    if ("Molecular Weight".equals(new String(ch, start, length))) {
                        this.inMolecularWeightInformation = true;
                    }
                } else if (this.inLogP && this.inLogPInformation) {
                    // set logP
                    this.logP = Double.parseDouble(new String(ch, start, length));
                    this.inLogP = false;
                    this.inLogPInformation = false;
                }
                break;
            }
            case "StringValueList": {
                if (this.inSynonyms && this.inSynonymsInformation) {
                    String potentialChebiIdentifier = new String(ch, start, length);
                    if (ChEBIIdentifier.PATTERN.matcher(potentialChebiIdentifier).matches()) {
                        // set chebi identifier
                        this.chebiIdentifier = new SimpleStringIdentifier(potentialChebiIdentifier);
                        this.inSynonyms = false;
                        this.inSynonymsInformation = false;
                    }
                }
                break;
            }
            case "NumValue": {
                if (this.inComputedProperties && this.inMolecularWeightInformation) {
                    // set molecular weight
                    this.molarMass = Double.parseDouble(new String(ch, start, length));
                    this.inMolecularWeightInformation = false;
                    this.inComputedProperties = false;
                }
                break;
            }
        }

    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        switch (this.currentTag) {
            case "RecordNumber":
            case "TOCHeading":
            case "StringValue":
            case "NumValue":
            case "StringValueList":
                this.currentTag = "";
                break;
        }

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        switch (qName) {
            case "RecordNumber":
            case "TOCHeading":
            case "StringValue":
            case "NumValue":
            case "StringValueList":
                this.currentTag = qName;
                break;
            case "Information":
                if (this.inRecordTitle) {
                    this.inRecordTitleInformation = true;
                } else
                if (this.inCanonicalSMILES) {
                    this.inCanonicalSMILESInformation = true;
                }
                if (this.inSynonyms) {
                    this.inSynonymsInformation = true;
                }
                if (this.inLogP) {
                    this.inLogPInformation = true;
                }
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

}
