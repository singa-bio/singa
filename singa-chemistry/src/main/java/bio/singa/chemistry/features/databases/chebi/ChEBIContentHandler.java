package bio.singa.chemistry.features.databases.chebi;

import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.structure.parser.mol.MolParser;
import bio.singa.chemistry.features.structure3d.Structure3D;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.structure.features.molarmass.MolarMass;
import bio.singa.structure.model.interfaces.Ligand;
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
    private Ligand ligand2D;
    private Ligand ligand3D;

    private StringBuilder structureBuilder;
    private boolean isInChemicalStructure;
    private boolean isMolStructure;
    private boolean is3DStructure;
    private boolean is2DStructure;
    private boolean isDefaultStrucutre;

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
        if (ligand3D != null) {
            species.setFeature(new Structure3D(ligand3D, ChEBIDatabase.DEGTYARENKO2008));
        } else if (ligand2D != null){
            species.setFeature(new Structure3D(ligand2D, ChEBIDatabase.DEGTYARENKO2008));
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
                if (is3DStructure && isMolStructure) {
                    // create structure
                    MolParser parser = new MolParser(Arrays.asList(structureBuilder.toString().split("\\R")));
                    ligand3D = parser.parseNextAsLigand();
                } else if (is2DStructure && isDefaultStrucutre){
                    // create structure
                    MolParser parser = new MolParser(Arrays.asList(structureBuilder.toString().split("\\R")));
                    ligand2D = parser.parseNextAsLigand();
                }
                // clean up
                structureBuilder = new StringBuilder();
                isInChemicalStructure = false;
                is2DStructure = false;
                is3DStructure = false;
                isMolStructure = false;
                isDefaultStrucutre = false;
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
                molarMass = new MolarMass(Double.valueOf(precursorString), ChEBIDatabase.DEGTYARENKO2008);
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
                    isDefaultStrucutre = true;
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
