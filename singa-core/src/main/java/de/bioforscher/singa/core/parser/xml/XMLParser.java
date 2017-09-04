package de.bioforscher.singa.core.parser.xml;

import de.bioforscher.singa.core.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public interface XMLParser extends Parser {

    String getResource();

    void setFetchResult(String fetchResult);

    default void fetchResourceWithQuery() {

        // create stream and connection
        BufferedReader inputStream = null;
        HttpURLConnection connection = null;
        StringBuilder fetchResult = new StringBuilder();

        // try to open stream
        try {
            connection = (HttpURLConnection) new URL(getResource())
                    .openConnection();

            // try to fetch result
            if (connection.getResponseCode() >= 400) {
                throw new IOException("received error code "
                        + connection.getResponseCode()
                        + " while reading RSS feed at " + getResource());
            } else {
                // using buffered reader to get correct encoding
                inputStream = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
            }

            String line;
            while (null != (line = inputStream.readLine())) {
                fetchResult.append(line).append("\r\n");
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set result
        setFetchResult(fetchResult.toString());

    }

}
