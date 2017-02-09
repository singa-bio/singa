package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.core.identifier.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @author cl
 */
class StructureContentIterator implements Iterator<List<String>> {

    private static final Logger logger = LoggerFactory.getLogger(StructureContentIterator.class);

    private static final String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";
    private static final String PDB_BASE_PATH = "data/structures/divided/pdb";
    private StructureParser.LocalPDB localPdb;

    enum SourceLocation {
        ONLINE, OFFLINE
    }

    private List<URL> identifiers;
    private List<Path> paths;

    private Iterator<URL> currentURL;
    private Iterator<Path> currentPath;

    private SourceLocation location;

    public StructureContentIterator(String identifier) {
        this(String.class, Collections.singletonList(identifier));
    }

    public StructureContentIterator(Path path) {
        this(Path.class, Collections.singletonList(path));
    }

    public StructureContentIterator(File file) {
        this(File.class, Collections.singletonList(file));
    }

    public StructureContentIterator(StructureParser.LocalPDB localPdb, String identifier) {
        this.localPdb = localPdb;
        this.paths = new ArrayList<>();
        prepareIdentifiers(Collections.singletonList(identifier));
    }

    public StructureContentIterator(StructureParser.LocalPDB localPdb, List<String> identifiers) {
        this.localPdb = localPdb;
        this.paths = new ArrayList<>();
        prepareLocalPDB(identifiers);
    }

    @SuppressWarnings("unchecked")
    public StructureContentIterator(Class<?> context, List<?> identifiers) {
        this.paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        if (context.equals(String.class)) {
            // identifiers for pdb online
            prepareIdentifiers((List<String>) identifiers);
        } else if (context.equals(Path.class)) {
            // paths
            prepareOfflinePaths((List<Path>) identifiers);
        } else if (context.equals(File.class)) {
            // files
            prepareOfflineFiles((List<File>) identifiers);
        }
    }

    private void prepareIdentifiers(List<String> identifiers) {
        for (String identifier : identifiers) {
            try {
                this.identifiers.add(new URL(String.format(PDB_FETCH_URL, identifier)));
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB", e);
            }
        }
        this.currentURL = this.identifiers.iterator();
        this.location = SourceLocation.ONLINE;
    }

    private void prepareOfflinePaths(List<Path> paths) {
        this.paths = paths;
        this.currentPath = this.paths.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    private void prepareOfflineFiles(List<File> files) {
        for (File file : files) {
            this.paths.add(file.toPath());
        }
        this.currentPath = this.paths.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    private void prepareLocalPDB(List<String> identifiers) {
        for (String identifier : identifiers) {
            this.paths.add(assemblePath(identifier));
        }
        this.currentPath = this.paths.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    private Path assemblePath(String identifier) {
        return this.localPdb.getLocalPdbPath().resolve(Paths.get(PDB_BASE_PATH + "/"
                + identifier.substring(1, identifier.length() - 1).toLowerCase()
                + "/pdb" + identifier.toLowerCase() + ".ent.gz"));
    }

    public InputStream readPacked(Path path) throws IOException {
        return new GZIPInputStream(new FileInputStream(path.toFile()));
    }

    @Override
    public boolean hasNext() {
        if (this.location == SourceLocation.ONLINE) {
            return this.currentURL.hasNext();
        } else {
            return this.currentPath.hasNext();
        }
    }

    @Override
    public List<String> next() {
        if (this.location == SourceLocation.OFFLINE) {
            try {
                Path path = this.currentPath.next();
                if (path.endsWith(".ent.gz")) {
                    return fetchLines(readPacked(path));
                } else {
                    return fetchLines(Files.newInputStream(path));
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for path.", e);
            }
        } else {
            try {
                return fetchLines(this.currentURL.next().openStream());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for URL.", e);
            }
        }
    }

    private List<String> fetchLines(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return bufferedReader.lines().collect(Collectors.toList());
            }
        }
    }

}
