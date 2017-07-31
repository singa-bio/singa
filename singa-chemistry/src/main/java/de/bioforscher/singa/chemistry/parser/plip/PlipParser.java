package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.core.parser.xml.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/**
 * @author cl
 */
public class PlipParser extends AbstractXMLParser<InteractionContainer> {

    private static final Logger logger = LoggerFactory.getLogger(UniProtParserService.class);
    private static final String BASE_URL = "https://biosciences.hs-mittweida.de/plip/interaction/";

    private static String secret;
    static {
        try {
            String line = new BufferedReader(new InputStreamReader(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("plip_credentials.txt"))).readLine();
            secret = new String(Base64.getMimeEncoder().encode(line.getBytes()));
            logger.info("PLIP Service is running against: {} - authentication provided", BASE_URL);
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("no credentials provided to access 'biosciences.hs-mittweida.de/plip/'");
        }
    }

    public PlipParser(String pdbIdentifier, String chainIdentifier, int residueNumber) {
        logger.info("Parsing interactions for {} chain {} residue {}", pdbIdentifier, chainIdentifier, residueNumber);
        getXmlReader().setContentHandler(new PlipContentHandler(pdbIdentifier));
        setResource(BASE_URL + pdbIdentifier + "/" + chainIdentifier + "/" + residueNumber);
    }

    public PlipParser(String pdbIdentifier, String chainIdentifier) {
        logger.info("Parsing interactions for {} chain {}", pdbIdentifier, chainIdentifier);
        getXmlReader().setContentHandler(new PlipContentHandler(pdbIdentifier));
        setResource(BASE_URL + "plain/" + pdbIdentifier+ "/" + chainIdentifier);
    }

    public PlipParser(String pdbIdentifier, InputStream inputStream) {
        logger.info("Parsing interactions for {}", pdbIdentifier);
        getXmlReader().setContentHandler(new PlipContentHandler(pdbIdentifier));
        setFetchResult(inputStream);
    }

    public static InteractionContainer parse(String pdbIdentifier, String chainIdentifier, int residueNumber) {
        PlipParser parser = new PlipParser(pdbIdentifier, chainIdentifier, residueNumber);
        return parser.parse();
    }

    public static InteractionContainer parse(String pdbIdentifier, String chainIdentifier) {
        PlipParser parser = new PlipParser(pdbIdentifier, chainIdentifier);
        return parser.parse();
    }

    public static InteractionContainer parse(String pdbIdentifier, InputStream inputStream) {
        PlipParser parser = new PlipParser(pdbIdentifier, inputStream);
        try {
           parser.getXmlReader().parse(new InputSource(parser.getFetchResult()));
           return ((PlipContentHandler) parser.getXmlReader().getContentHandler()).getInteractionContainer();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse xml from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void fetchResource() {
        logger.debug("querying PLIP-rest-service for {}", getResource());
        try {
            URL url = new URL(getResource());
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + secret);
            connection.connect();
            setFetchResult(connection.getInputStream());
        } catch (MalformedURLException e) {
            throw new UncheckedIOException("The url \"" + getResource() + "\" seems to be malformed", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not connect to \"" + getResource() + "\", the server seems to be unavailable.", e);
        }
    }

    @Override
    public InteractionContainer parse() {
        parseXML();
        return ((PlipContentHandler) this.getXmlReader().getContentHandler()).getInteractionContainer();
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
}
