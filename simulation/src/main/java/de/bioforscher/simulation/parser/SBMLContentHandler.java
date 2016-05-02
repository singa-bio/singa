package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.simulation.reactions.EnzymeReaction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SBMLContentHandler implements ContentHandler {

    // TODO this can be a singleton
    private static final ChEBIParserService chebiParserService = new ChEBIParserService();

    // enzyme
    private String enzymeName;
    private UniProtIdentifier enzymeIdentifier;
    private double turnoverNumber;
    private double michaelisConstant;
    private Species criticalSubstrate;

    // reaction
    private EnzymeReaction reaction;


    // temporary data
    private String currentSpeciesId;
    private String currentSpeciesName;
    private Map<String, Species> speciesDictionary;

    private boolean inSpeciesList;
    private boolean inReactantsList;
    private boolean inProductsList;
    private boolean newSpecies;

    SBMLContentHandler() {
        this.speciesDictionary = new HashMap<>();
        // TODO builder pattern for reactions
        // TODO TEST ME
        this.reaction = new EnzymeReaction.Builder().build();
        this.currentSpeciesId = "";
        this.inSpeciesList = false;
        this.inReactantsList = false;
        this.inProductsList = false;
        this.newSpecies = false;
    }

    public EnzymeReaction getReaction() {
        // build enzyme
        Enzyme enzyme = new Enzyme.Builder(enzymeIdentifier)
                .name(enzymeName)
                .turnoverNumber(this.turnoverNumber)
                .michaelisConstant(this.michaelisConstant)
                .criticalSubstrate(this.criticalSubstrate)
                .build();
        this.reaction.setEnzyme(enzyme);
        return this.reaction;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        switch (qName) {
            case "listOfSpecies": {
                this.inSpeciesList = false;
            }
            case "species": {
                this.newSpecies = false;
            }
            case "listOfReactants": {
                this.inReactantsList = false;
            }
            case "listOfProducts": {
                this.inReactantsList = false;
            }
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
            case "listOfSpecies": {
                this.inSpeciesList = true;
                break;
            }
            case "species": {
                if (this.inSpeciesList) {
                    this.currentSpeciesId = atts.getValue("id");
                    this.currentSpeciesName = atts.getValue("name");
                    this.newSpecies = true;
                }
                break;
            }
            case "rdf:li": {
                if (this.newSpecies) {
                    String resource = atts.getValue("rdf:resource");
                    // species
                    Pattern patternCHEBI = Pattern.compile("chebi/(CHEBI:[\\d]+)");
                    Matcher matcherCHEBI = patternCHEBI.matcher(resource);
                    if (matcherCHEBI.find()) {
                        String identifier = matcherCHEBI.group(1);
                        chebiParserService.setResource(identifier);
                        Species Species = chebiParserService.fetchSpecies();
                        if (Species != null) {
                            this.speciesDictionary.put(currentSpeciesId, Species);
                            this.newSpecies = false;
                        }
                    }
                    // enzyme
                    Pattern patternUniProt = Pattern.compile("uniprot/(.+)");
                    Matcher matcherUniProt = patternUniProt.matcher(resource);
                    if (matcherUniProt.find()) {
                        String uniProtId = matcherUniProt.group(1);
                        this.enzymeIdentifier = new UniProtIdentifier(uniProtId);
                        this.enzymeName = currentSpeciesName;
                        this.newSpecies = false;
                    }
                }
                break;
            }
            case "listOfReactants": {
                this.inReactantsList = true;
                break;
            }
            case "listOfProducts": {
                this.inProductsList = true;
                break;
            }
            case "speciesReference": {
                int stoichiometry = Integer.parseInt(atts.getValue("stoichiometry"));
                String speciesId = atts.getValue("species");
                this.reaction.getStoichiometricCoefficients().put(
                        speciesDictionary.get(speciesId), stoichiometry);
                if (this.inReactantsList) {
                    this.reaction.getSubstrates().add(
                            speciesDictionary.get(speciesId));
                } else if (this.inProductsList) {
                    this.reaction.getProducts().add(
                            speciesDictionary.get(speciesId));
                }
                break;
            }
            case "localParameter": {
                String id = atts.getValue("id");
                // turnover number
                Pattern patternKCat = Pattern.compile("kcat",
                        Pattern.CASE_INSENSITIVE);
                Matcher matcherKCat = patternKCat.matcher(id);
                if (matcherKCat.find()) {
                    this.turnoverNumber = Double.valueOf(atts.getValue("value"));
                }
                // michaelis constant
                Pattern patternKM = Pattern.compile("km_(.+)",
                        Pattern.CASE_INSENSITIVE);
                Matcher matcherKM = patternKM.matcher(id);
                if (matcherKM.find()) {
                    String speciesId = matcherKM.group(1);
                    // critical substrate
                    this.criticalSubstrate = speciesDictionary.get(speciesId);
                    this.michaelisConstant = Double.valueOf(atts.getValue("value"));
                }
            }

        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {

    }

}
