package bio.singa.structure.parser.sifts;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class ResidueMapContentHandler implements ContentHandler {

    private static final Pattern insertionCode = Pattern.compile("(\\d+)(\\p{Alpha}*)");
    public Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>> mapping;
    private String currentTag;

    private String currentPdbid;
    private int currentModel = 1;
    private String currentChain;
    private int currentResidue;
    private int mappedResidue;
    private char currentInsertionCode;
    private UniProtIdentifier currentUniProtIdentifier;

    private boolean inResidue;
    private boolean skip;

    public ResidueMapContentHandler(String currentPdbid) {
        this.currentPdbid = currentPdbid;
        mapping = new HashMap<>();
    }

    public Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>> getMapping() {
        return mapping;
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
        // skip entry if no pdb reference is there
        if (!skip) {
            switch (qName) {
                case "residue": {
                    inResidue = true;
                }
                case "crossRefDb": {
                    if (inResidue) {
                        String dbSource = atts.getValue("dbSource");
                        if (dbSource.equals("PDB")) {
                            final String dbResNum = atts.getValue("dbResNum");
                            currentChain = atts.getValue("dbChainId");
                            if (dbResNum.equals("null")) {
                                skip = true;
                            } else {
                                Matcher matcher = insertionCode.matcher(dbResNum);
                                if (matcher.matches()) {
                                    currentResidue = Integer.parseInt(matcher.group(1));
                                    currentInsertionCode = matcher.group(2).isEmpty() ? PdbLeafIdentifier.DEFAULT_INSERTION_CODE : matcher.group(2).charAt(0);
                                }
                            }
                        } else if (dbSource.equals("UniProt")) {
                            final String dbResNum = atts.getValue("dbResNum");
                            currentUniProtIdentifier = new UniProtIdentifier(atts.getValue("dbAccessionId"));
                            mappedResidue = Integer.parseInt(dbResNum);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "residue": {
                // finalize mapping entry
                if (skip) {
                    skip = false;
                } else {
                    if (!mapping.containsKey(currentUniProtIdentifier)) {
                        mapping.put(currentUniProtIdentifier, new TreeMap<>());
                    }
                    PdbLeafIdentifier leafIdentifier = new PdbLeafIdentifier(currentPdbid, currentModel, currentChain, currentResidue, currentInsertionCode);
                    mapping.get(currentUniProtIdentifier).put(leafIdentifier, mappedResidue);
                }
                inResidue = false;
            }
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {

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
