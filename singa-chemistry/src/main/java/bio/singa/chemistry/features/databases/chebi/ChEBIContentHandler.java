package bio.singa.chemistry.features.databases.chebi;

import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.chemistry.features.structure2d.MolParser2D;
import bio.singa.chemistry.features.structure2d.Structure2D;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.quantities.MolarMass;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

import java.util.Arrays;

/**
 * @author cl
 */
public class ChEBIContentHandler implements ContentHandler {

    private String currentTag = "";

    private String primaryIdentifier;
    private SimpleStringIdentifier identifier;
    private String name;
    private MolarMass molarMass;
    private StringBuilder smilesBuilder;
    private InChIKey inChIKey;
    private MoleculeGraph structure;

    private StringBuilder structureBuilder;
    private boolean isInChemicalStructure;
    private boolean isMolStructure;
    private boolean is3DStructure;
    private boolean is2DStructure;
    private boolean isDefaultStructure;

    public ChEBIContentHandler() {
        smilesBuilder = new StringBuilder();
        structureBuilder = new StringBuilder();
    }

    public ChEBIContentHandler(String primaryIdentifier) {
        this();
        this.primaryIdentifier = primaryIdentifier;
    }

    public SmallMolecule getSpecies() {
        if (primaryIdentifier == null) {
            primaryIdentifier = identifier.toString();
        }

        SmallMolecule species = SmallMolecule.create(primaryIdentifier)
                .additionalIdentifier(inChIKey)
                .build();
        if (name != null) {
            species.addName(name);
        }
        if (! smilesBuilder.toString().isEmpty()) {
            species.setFeature(new Smiles(smilesBuilder.toString(), ChEBIDatabase.DEGTYARENKO2008));
        }
        if (molarMass != null) {
            species.setFeature(molarMass);
        }
        if (structure != null){
            species.setFeature(new Structure2D(structure, ChEBIDatabase.DEGTYARENKO2008));
        }
        return species;
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
            case "ChemicalStructures":
                isInChemicalStructure = true;
                // skip all of th following if this is not in section chemical structure (for whatever reason)
            case "structure":
            case "type":
            case "dimension":
            case "defaultStructure":
                if (!isInChemicalStructure) {
                    break;
                }
                // fall through set tag
            case "chebiId":
            case "chebiAsciiName":
            case "mass":
            case "smiles":
            case "inchiKey":
                currentTag = qName;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "ChemicalStructures":
                if (is2DStructure && isDefaultStructure){
                    // create structure
                    MolParser2D parser = new MolParser2D(Arrays.asList(structureBuilder.toString().split("\\R")));
                    structure = parser.parseNextMoleculeGraph();
                }
                // clean up
                structureBuilder = new StringBuilder();
                isInChemicalStructure = false;
                is2DStructure = false;
                is3DStructure = false;
                isMolStructure = false;
                isDefaultStructure = false;
            case "structure":
            case "type":
            case "dimension":
            case "defaultStructure":
            case "chebiId":
            case "chebiAsciiName":
            case "mass":
            case "smiles":
            case "inchiKey":
                currentTag = "";
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        switch (currentTag) {
            case "chebiId": {
                if (identifier == null) {
                    final String precursorString = new String(ch, start, length);
                    identifier = new SimpleStringIdentifier(precursorString);
                }
                break;
            }
            case "chebiAsciiName": {
                name = new String(ch, start, length);
                break;
            }
            case "mass": {
                final String precursorString = new String(ch, start, length);
                molarMass = MolarMass.of(Double.parseDouble(precursorString), MolarMass.GRAM_PER_MOLE)
                        .evidence(ChEBIDatabase.DEGTYARENKO2008)
                        .build();
                break;
            }
            case "smiles": {
                smilesBuilder.append(new String(ch, start, length));
                break;
            }
            case "inchiKey": {
                final String precursorString = new String(ch, start, length);
                inChIKey = new InChIKey(precursorString);
                break;
            }
            case "type": {
                final String string = new String(ch, start, length);
                if (string.equals("mol")) {
                    isMolStructure = true;
                }
                break;
            }
            case "dimension": {
                final String string = new String(ch, start, length);
                if (string.equals("2D")) {
                    is2DStructure = true;
                } else if (string.equals("3D")) {
                    is3DStructure = true;
                }
                break;
            }
            case "defaultStructure": {
                final String string = new String(ch, start, length);
                if (string.equals("true")) {
                    isDefaultStructure = true;
                }
            }
            case "structure": {
                structureBuilder.append(new String(ch, start, length));
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
