package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
    private List<URL> identifiers;
    private List<Path> paths;
    private List<String> pdbIds;
    private Iterator<String> pdbIdIterator;
    private String currentPdbIdentifier;
    private List<String> chains;
    private Iterator<String> chainIterator;
    private String currentChain;
    private Iterator<URL> currentURL;
    private Iterator<Path> currentPath;
    private SourceLocation location;
    private int progressCounter;


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
        this.pdbIds = new ArrayList<>();
        prepareLocalPDB(Collections.singletonList(identifier));
    }

    public StructureContentIterator(StructureParser.LocalPDB localPdb, List<String> identifiers) {
        this.localPdb = localPdb;
        this.paths = new ArrayList<>();
        this.pdbIds = new ArrayList<>();
        prepareLocalPDB(identifiers);
    }

    public StructureContentIterator(List<Pair<String>> mapping, StructureParser.LocalPDB localPDB) {
        // sorry for the argument switch to work around erasure :(
        this.localPdb = localPDB;
        this.paths = new ArrayList<>();
        this.pdbIds = new ArrayList<>();
        this.chains = new ArrayList<>();
        prepareMappedLocalPDB(mapping);
    }

    public StructureContentIterator(List<Pair<String>> mapping) {
        this.paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.pdbIds = new ArrayList<>();
        this.chains = new ArrayList<>();
        prepareMappedIdentifiers(mapping);
    }

    /**
     * Creates a new Iterator based on the context of parsing.
     *
     * @param context     The context.
     * @param identifiers The pdbIdentifier to parse
     */
    @SuppressWarnings("unchecked")
    public StructureContentIterator(Class<?> context, List<?> identifiers) {
        this.paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.pdbIds = new ArrayList<>();
        this.progressCounter = 0;

        if (context.equals(String.class)) {
            // pdbIdentifiers for pdb online
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
                this.pdbIds.add(identifier);
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB", e);
            }
        }
        this.currentURL = this.identifiers.iterator();
        this.pdbIdIterator = this.pdbIds.iterator();
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
            this.pdbIds.add(identifier);
        }
        this.currentPath = this.paths.iterator();
        this.pdbIdIterator = this.pdbIds.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    private void prepareMappedLocalPDB(List<Pair<String>> mapping) {
        for (Pair<String> pair : mapping) {
            this.paths.add(assemblePath(pair.getFirst()));
            this.pdbIds.add(pair.getFirst());
            this.chains.add(pair.getSecond());
        }
        this.currentPath = this.paths.iterator();
        this.pdbIdIterator = this.pdbIds.iterator();
        this.chainIterator = this.chains.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    private void prepareMappedIdentifiers(List<Pair<String>> mapping) {
        for (Pair<String> pair : mapping) {
            try {
                this.identifiers.add(new URL(String.format(PDB_FETCH_URL, pair.getFirst())));
                this.pdbIds.add(pair.getFirst());
                this.chains.add(pair.getSecond());
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB", e);
            }
        }
        this.currentURL = this.identifiers.iterator();
        this.pdbIdIterator = this.pdbIds.iterator();
        this.chainIterator = this.chains.iterator();
        this.location = SourceLocation.ONLINE;
    }

    private Path assemblePath(String identifier) {
        return this.localPdb.getLocalPdbPath().resolve(Paths.get(PDB_BASE_PATH + "/"
                + identifier.substring(1, identifier.length() - 1).toLowerCase()
                + "/pdb" + identifier.toLowerCase() + ".ent.gz"));
    }

    public InputStream readPacked(Path path) throws IOException {
        return new GZIPInputStream(new FileInputStream(path.toFile()));
    }

    /**
     * Returns the the number of structures enqueued structures to parse.
     *
     * @return The the number of structures enqueued structures to parse.
     */
    public int getNumberOfQueuedStructures() {
        if (this.location == SourceLocation.ONLINE) {
            return this.identifiers.size();
        } else {
            return this.paths.size();
        }
    }

    /**
     * Returns the the number of structures that still have to be parsed.
     *
     * @return The the number of structures still to be parsed.
     */
    public int getNumberOfRemainingStructures() {
        if (this.location == SourceLocation.ONLINE) {
            return this.identifiers.size() - this.progressCounter;
        } else {
            return this.paths.size() - this.progressCounter;
        }
    }

    public String getCurrentPdbIdentifier() {
        if (this.currentPdbIdentifier != null) {
            return this.currentPdbIdentifier;
        } else {
            throw new IllegalStateException("Unable to retrieve the PDB Identifier in the current state.");
        }
    }

    public String getCurrentChainIdentifier() {
        if (this.currentChain != null) {
            return this.currentChain;
        } else {
            throw new IllegalStateException("Unable to retrieve chain Identifier in the current state.");
        }

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
        if (this.pdbIds != null && !this.pdbIds.isEmpty()) {
            // TODO speed this up by not parsing the same id twice but using previous information
            this.currentPdbIdentifier = this.pdbIdIterator.next();
            if (this.chains != null) {
                this.currentChain = this.chainIterator.next();
                logger.debug("parsing structure {}/{}", this.currentPdbIdentifier, this.currentChain);
            } else {
                logger.debug("parsing structure {}");
            }
        }
        if (this.location == SourceLocation.OFFLINE) {
            try {
                Path path = this.currentPath.next();
                if (path.toString().endsWith(".ent.gz")) {
                    return fetchLines(readPacked(path));
                } else {
                    return fetchLines(Files.newInputStream(path));
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for path.", e);
            } finally {
                this.progressCounter++;
            }
        } else {
            try {
                return fetchLines(this.currentURL.next().openStream());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for URL.", e);
            } finally {
                this.progressCounter++;
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

    private enum SourceLocation {
        ONLINE, OFFLINE
    }
}
