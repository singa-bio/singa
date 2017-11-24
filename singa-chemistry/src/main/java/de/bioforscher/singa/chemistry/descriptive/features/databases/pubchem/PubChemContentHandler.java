package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.PubChemIdentifier;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
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
        currentTag = "";
    }

    public Species getSpecies() {
        return new Species.Builder(chebiIdentifier)
                .name(name)
                .assignFeature(new MolarMass(molarMass, PubChemDatabase.origin))
                .assignFeature(new Smiles(smilesRepresentation, PubChemDatabase.origin))
                .assignFeature(new LogP(logP, PubChemDatabase.origin))
                .additionalIdentifier(new PubChemIdentifier("CID:" + pubChemIdentifier))
                .build();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        switch (currentTag) {
            case "RecordNumber": {
                // set pubchem identifier
                pubChemIdentifier = new String(ch, start, length);
                break;
            }
            case "TOCHeading": {
                String value = new String(ch, start, length);
                switch (value) {
                    case "Record Title":
                        inRecordTitle = true;
                        break;
                    case "Canonical SMILES":
                        inCanonicalSMILES = true;
                        break;
                    case "Computed Properties":
                        inComputedProperties = true;
                        break;
                    case "Depositor-Supplied Synonyms":
                        inSynonyms = true;
                        break;
                    case "LogP":
                        inLogP = true;
                        break;
                }
                break;
            }
            case "StringValue": {
                if (inRecordTitle && inRecordTitleInformation) {
                    // set name
                    name = new String(ch, start, length);
                    inRecordTitle = false;
                    inRecordTitleInformation = false;
                } else if (inCanonicalSMILES && inCanonicalSMILESInformation) {
                    // set smiles
                    smilesRepresentation = new String(ch, start, length);
                    inCanonicalSMILES = false;
                    inCanonicalSMILESInformation = false;
                } else if (inComputedProperties) {
                    // set logP
                    if ("Molecular Weight".equals(new String(ch, start, length))) {
                        inMolecularWeightInformation = true;
                    }
                } else if (inLogP && inLogPInformation) {
                    // set logP
                    String logPString = new String(ch, start, length);
                    // explicitly stated as log KOW
                    if (logPString.contains("log Kow")) {
                        // remove "non double characters" (very simple for now)
                        String cleanedString = logPString.replaceAll("[^0-9.]", "");
                        logP = Double.parseDouble(cleanedString);
                        inLogP = false;
                        inLogPInformation = false;
                    } else if (logPString.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
                        logP = Double.parseDouble(logPString);
                        inLogP = false;
                        inLogPInformation = false;
                    }
                }
                break;
            }
            case "StringValueList": {
                if (inSynonyms && inSynonymsInformation) {
                    String potentialChebiIdentifier = new String(ch, start, length);
                    if (ChEBIIdentifier.PATTERN.matcher(potentialChebiIdentifier).matches()) {
                        // set chebi identifier
                        chebiIdentifier = new SimpleStringIdentifier(potentialChebiIdentifier);
                        inSynonyms = false;
                        inSynonymsInformation = false;
                    }
                }
                break;
            }
            case "NumValue": {
                if (inComputedProperties && inMolecularWeightInformation) {
                    // set molecular weight
                    molarMass = Double.parseDouble(new String(ch, start, length));
                    inMolecularWeightInformation = false;
                    inComputedProperties = false;
                } else if (inLogP && inLogPInformation) {
                    // set logP
                    String logPString = new String(ch, start, length);
                    // explicitly stated as log KOW
                    logP = Double.parseDouble(logPString);
                    inLogP = false;
                    inLogPInformation = false;
                }
                break;
            }
        }

    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch (currentTag) {
            case "RecordNumber":
            case "TOCHeading":
            case "StringValue":
            case "NumValue":
            case "StringValueList":
                currentTag = "";
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
                currentTag = qName;
                break;
            case "Information":
                if (inRecordTitle) {
                    inRecordTitleInformation = true;
                } else if (inCanonicalSMILES) {
                    inCanonicalSMILESInformation = true;
                }
                if (inSynonyms) {
                    inSynonymsInformation = true;
                }
                if (inLogP) {
                    inLogPInformation = true;
                }
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

}
