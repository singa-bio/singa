package de.bioforscher.chemistry.parser;

import de.bioforscher.core.identifier.ChEBIIdentifier;
import de.bioforscher.core.parser.rest.AbstractRESTParser;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChEBIImageService extends AbstractRESTParser {

    private static final int dafaultImageWidth = 75;
    private InputStream imageStream;

    public ChEBIImageService(String chEBIIdentifier) {
        this(dafaultImageWidth, new ChEBIIdentifier(chEBIIdentifier));
    }

    public ChEBIImageService(ChEBIIdentifier chEBIIdentifier) {
        this(dafaultImageWidth, chEBIIdentifier);
    }

    public ChEBIImageService(int imageWidth, ChEBIIdentifier chEBIIdentifier) {
        setResource("http://www.ebi.ac.uk/chebi/displayImage.do;jsessionid=test");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("defaultImage", "true");
        queryMap.put("imageIndex", "0");
        queryMap.put("chebiId", String.valueOf(chEBIIdentifier.getConsecutiveNumber()));
        queryMap.put("dimensions", String.valueOf(imageWidth));
        setQueryMap(queryMap);
    }

    @Override
    public void fetchResource() {
        // create client
        Client client = ClientBuilder.newClient();
        WebTarget targetResource = client.target(getResource());

        // build query
        Map<String, String> queryMap = getQueryMap();
        WebTarget query = null;
        for (String target : queryMap.keySet()) {
            if (query == null) {
                query = targetResource.queryParam(target, queryMap.get(target));
            } else {
                query = query.queryParam(target, queryMap.get(target));
            }
        }

        assert query != null;
        Invocation.Builder invocationBuilder = query.request(MediaType.WILDCARD);

        // get response
        Response response = invocationBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            this.imageStream = response.readEntity(InputStream.class);
        } else {
            throw new WebApplicationException("Http call failed. response code is" + response.getStatus()
                    + ". Error reported is " + response.getStatusInfo());
        }
    }

    public void saveImageToFile(String filePath) {
        File downloadedFile = new File(filePath);
        try {
            Files.copy(this.imageStream, downloadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("the file details after call: " + downloadedFile.getAbsolutePath() + ", size is "
                + downloadedFile.length());
    }

    public InputStream getImageStream() {
        return this.imageStream;
    }

    @Override
    public List<Object> parseObjects() {
        return null;
    }


}
