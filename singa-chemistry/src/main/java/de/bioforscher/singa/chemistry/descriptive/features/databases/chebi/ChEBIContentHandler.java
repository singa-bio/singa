package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.chemistry.descriptive.features.structure3d.MolStructueParser;
import de.bioforscher.singa.chemistry.descriptive.features.structure3d.Structure3D;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.InChIKey;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import de.bioforscher.singa.structure.model.interfaces.Ligand;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

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

    public Species getSpecies() {
        if (primaryIdentifier == null) {
            primaryIdentifier = identifier.toString();
        }

        Species species = new Species.Builder(primaryIdentifier)
                .name(name)
                .assignFeature(new Smiles(smilesBuilder.toString(), ChEBIDatabase.origin))
                .additionalIdentifier(new ChEBIIdentifier(identifier.toString()))
                .additionalIdentifier(inChIKey)
                .build();

        if (molarMass != null) {
            species.setFeature(molarMass);
        }
        if (ligand3D != null) {
            species.setFeature(new Structure3D(ligand3D, ChEBIDatabase.origin));
        } else if (ligand2D != null){
            species.setFeature(new Structure3D(ligand2D, ChEBIDatabase.origin));
        }
        return species;
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
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "ChemicalStructures":
                if (is3DStructure && isMolStructure) {
                    // create structure
                    MolStructueParser parser = new MolStructueParser(Arrays.asList(structureBuilder.toString().split("\\R")));
                    ligand3D = parser.parse();
                } else if (is2DStructure && isDefaultStrucutre){
                    // create structure
                    MolStructueParser parser = new MolStructueParser(Arrays.asList(structureBuilder.toString().split("\\R")));
                    ligand2D = parser.parse();
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
    public void characters(char[] ch, int start, int length) throws SAXException {
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
                molarMass = new MolarMass(Double.valueOf(precursorString), ChEBIDatabase.origin);
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
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

}
