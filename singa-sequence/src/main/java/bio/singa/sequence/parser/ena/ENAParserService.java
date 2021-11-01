package bio.singa.sequence.parser.ena;


import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.sequence.model.NucleotideSequence;
import bio.singa.sequence.model.SequenceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author cl
 * @deprecated ENA does not provide xml files anymore it seems
 */
public class ENAParserService extends AbstractXMLParser<NucleotideSequence> {

    private static final Logger logger = LoggerFactory.getLogger(ENAParserService.class);
    private static final String ENA_FETCH_URL = "https://www.ebi.ac.uk/ena/data/view/%s&display=xml";

    public ENAParserService(ENAAccessionNumber enaAccessionNumber) {
        getXmlReader().setContentHandler(new ENAContentHandler(enaAccessionNumber));
        setResource(String.format(ENA_FETCH_URL, enaAccessionNumber.getContent()));
    }

    public static NucleotideSequence parse(String enaAccessionNumber) {
        return parse(new ENAAccessionNumber(enaAccessionNumber));
    }

    public static NucleotideSequence parse(ENAAccessionNumber enaAccessionNumber) {
        logger.info("Parsing sequence with identifier {} from ENA.", enaAccessionNumber.getContent());
        ENAParserService parser = new ENAParserService(enaAccessionNumber);
        return parser.parse();
    }

    public static SequenceContainer parseGeneTranslationPair(String enaAccessionNumber) {
        return parseGeneTranslationPair(new ENAAccessionNumber(enaAccessionNumber));
    }

    public static SequenceContainer parseGeneTranslationPair(ENAAccessionNumber enaAccessionNumber) {
        logger.info("Parsing sequence with identifier {} from ENA.", enaAccessionNumber.getContent());
        ENAParserService parser = new ENAParserService(enaAccessionNumber);
        parser.parseXML();
        ENAContentHandler contentHandler = (ENAContentHandler) parser.getXmlReader().getContentHandler();
        SequenceContainer container = new SequenceContainer();
        container.addSequence(SequenceContainer.GENE, contentHandler.getNucleotideSequence());
        container.addSequence(SequenceContainer.TRANSLATION, contentHandler.getTranslationSequence());
        return container;
    }


    private void parseXML() {
        fetchResource();
        // parse xml
        try {
            getXmlReader().parse(new InputSource(getFetchResult()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse xml from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NucleotideSequence parse() {
        parseXML();
        return ((ENAContentHandler) getXmlReader().getContentHandler()).getNucleotideSequence();
    }



}
