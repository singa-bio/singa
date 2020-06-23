package bio.singa.chemistry.features.databases.pubchem;

import bio.singa.chemistry.features.logp.LogP;
import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.quantities.MolarMass;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

import java.util.ArrayList;
import java.util.List;

class PubChemContentHandler implements ContentHandler {

    // species attributes
    private PubChemIdentifier pubChemIdentifier;
    private String name;

    // parser attributes
    private String currentTag;

    // reading SMILES
    private String smiles;
    private boolean inSmiles;

    // reading molar mass
    private double molarMass;
    private boolean inMolarMass;

    // reading logP value
    private double logP;
    private boolean inLogP;

    // reading additional identifiers
    private List<Identifier> identifiers;
    private boolean inSynonyms;


    public PubChemContentHandler() {
        currentTag = "";
        identifiers = new ArrayList<>();
    }

    public SmallMolecule getSpecies() {
        SmallMolecule species = SmallMolecule.create(pubChemIdentifier.getContent())
                .build();
        if (name != null) {
            species.addName(name);
        }
        if (molarMass != 0.0) {
            species.setFeature(MolarMass.of(molarMass, MolarMass.GRAM_PER_MOLE)
                    .evidence(PubChemDatabase.evidence)
                    .build());
        }
        if (logP != 0.0) {
            species.setFeature(new LogP(logP, PubChemDatabase.evidence));
        }
        if (smiles != null && !smiles.isEmpty()) {
            species.setFeature(new Smiles(smiles, PubChemDatabase.evidence));
        }
        identifiers.forEach(species::addAdditionalIdentifier);
        return species;

    }

    @Override
    public void characters(char[] ch, int start, int length) {
        switch (currentTag) {
            case "RecordNumber": {
                // set pubchem identifier
                pubChemIdentifier = new PubChemIdentifier("CID:" + new String(ch, start, length));
                break;
            }
            case "RecordTitle": {
                // set name
                name = new String(ch, start, length);
            }
            case "TOCHeading": {
                String value = new String(ch, start, length);
                switch (value) {
                    case "Molecular Weight":
                        inMolarMass = true;
                        break;
                    case "Canonical SMILES":
                        inSmiles = true;
                        break;
                    case "Depositor-Supplied Synonyms":
                        inSynonyms = true;
                        break;
                    case "Octanol/Water Partition Coefficient":
                        inLogP = true;
                        break;
                }
                break;
            }
            case "Name": {
                String value = new String(ch, start, length);
                switch (value) {
                    case "Depositor-Supplied Synonyms":
                        inSynonyms = true;
                        break;
                }
                break;
            }
            case "String": {
                if (inSmiles) {
                    // set smiles
                    smiles = new String(ch, start, length);
                    inSmiles = false;
                } else if (inLogP) {
                    // set logP
                    String logPString = new String(ch, start, length);
                    if (logPString.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
                        logP = Double.parseDouble(logPString);
                        inLogP = false;
                    }
                } else if (inSynonyms) {
                    String potentialIdentifier = new String(ch, start, length);
                    if (ChEBIIdentifier.PATTERN.matcher(potentialIdentifier).matches()) {
                        identifiers.add(new ChEBIIdentifier(potentialIdentifier, PubChemDatabase.evidence));
                    }
                    if (InChIKey.PATTERN.matcher(potentialIdentifier).matches()) {
                        identifiers.add(new InChIKey(potentialIdentifier, PubChemDatabase.evidence));
                    }
                }
                break;
            }
            case "Number": {
                if (inMolarMass) {
                    // set molecular weight
                    molarMass = Double.parseDouble(new String(ch, start, length));
                    inMolarMass = false;
                } else if (inLogP) {
                    // set logP
                    String logPString = new String(ch, start, length);
                    if (logPString.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
                        logP = Double.parseDouble(logPString);
                        inLogP = false;
                    }
                }
                break;
            }
        }

    }

    @Override
    public void endDocument() {
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        switch (currentTag) {
            case "RecordNumber":
            case "RecordTitle":
            case "TOCHeading":
            case "String":
            case "Number":
                currentTag = "";
                break;
            case "Section": {
                inMolarMass = false;
                inSmiles = false;
                inSynonyms = false;
                inLogP = false;
                currentTag = "";
                break;
            }

        }

    }

    @Override
    public void endPrefixMapping(String prefix) {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    @Override
    public void processingInstruction(String target, String data) {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) {
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) {

        switch (qName) {
            case "RecordNumber":
            case "RecordTitle":
            case "TOCHeading":
            case "String":
            case "Number":
            case "Section":
                currentTag = qName;
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
    }

}
