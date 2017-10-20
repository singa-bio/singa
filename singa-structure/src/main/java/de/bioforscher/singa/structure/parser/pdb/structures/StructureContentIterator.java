package de.bioforscher.singa.structure.parser.pdb.structures;

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
 * This iterator administers each structure that should be parsed and its origin.
 *
 * @author cl
 */
class StructureContentIterator implements Iterator<List<String>> {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(StructureContentIterator.class);

    /**
     * The URL the where pdb files are parsed from.
     */
    private static final String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";

    /**
     * The location the files should be parsed from.
     */
    private enum SourceLocation {
        ONLINE, OFFLINE
    }

    /**
     * The local pdb, if there is any.
     */
    private StructureParser.LocalPDB localPdb;

    /**
     * The urls that are to be parsed.
     */
    private List<URL> identifiers;

    /**
     * The iterator that traverses the urls.
     */
    private Iterator<URL> currentURL;

    /**
     * The paths the structures are supplied from.
     */
    private List<Path> paths;

    /**
     * The iterator that traverses the paths.
     */
    private Iterator<Path> currentPath;

    /**
     * The pdb identifiers that are to be parsed, if they can be inferred.
     */
    private List<String> pdbIdentifiers;

    /**
     * The iterator that traverses the pdb identifiers.
     */
    private Iterator<String> pdbIdentifierIterator;

    /**
     * The pdb identifier that is to be parsed next.
     */
    private String currentPdbIdentifier;

    /**
     * The chains that are to be parsed for specific pdb files.
     */
    private List<String> chains;

    /**
     * The iterator that traverses the chain identifiers
     */
    private Iterator<String> chainIdentifierIterator;

    /**
     * The chain identifier that is to be parsed next.
     */
    private String currentChainIdentifier;

    /**
     * The general location of the source files.
     */
    private SourceLocation location;

    /**
     * The current source.
     */
    private String currentSource;

    /**
     * The number of structures that have already be processed.
     */
    private int progressCounter;

    /**
     * Creates a new structure content iterator for a single identifier.
     *
     * @param identifier The identifier that is to be parsed.
     */
    StructureContentIterator(String identifier) {
        this(String.class, Collections.singletonList(identifier));
    }

    /**
     * Creates a new structure content iterator for a single path.
     *
     * @param path The path of a file that is to be parsed.
     */
    StructureContentIterator(Path path) {
        this(Path.class, Collections.singletonList(path));
    }

    /**
     * Creates a new structure content iterator for a single file.
     *
     * @param file The  file that is to be parsed.
     */
    StructureContentIterator(File file) {
        this(File.class, Collections.singletonList(file));
    }

    /**
     * Creates a new structure content iterator to parse a specific pdb file from a local pbd installation.
     *
     * @param localPdb The pdb installation.
     * @param identifier The identifier that is to be parsed.
     */
    StructureContentIterator(StructureParser.LocalPDB localPdb, String identifier) {
        this.localPdb = localPdb;
        this.paths = new ArrayList<>();
        this.pdbIdentifiers = new ArrayList<>();
        prepareLocalPDB(Collections.singletonList(identifier));
    }

    /**
     * Creates a new structure content iterator to parse multiple pdb files from a local pdb installation.
     *
     * @param localPdb The pdb installation.
     * @param identifiers The identifiers that are to be parsed.
     */
    StructureContentIterator(StructureParser.LocalPDB localPdb, List<String> identifiers) {
        this.localPdb = localPdb;
        this.paths = new ArrayList<>();
        this.pdbIdentifiers = new ArrayList<>();
        prepareLocalPDB(identifiers);
    }

    /**
     * Creates a new structure content iterator to parse specific cains of proteins from a local pdb installation.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     * @param localPDB The pdb installation.
     */
    StructureContentIterator(List<Pair<String>> mapping, StructureParser.LocalPDB localPDB) {
        // sorry for the argument switch to work around erasure :(
        this.localPdb = localPDB;
        this.paths = new ArrayList<>();
        this.pdbIdentifiers = new ArrayList<>();
        this.chains = new ArrayList<>();
        prepareMappedLocalPDB(mapping);
    }

    /**
     * Creates a new structure content iterator to parse specific cains of proteins online.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    StructureContentIterator(List<Pair<String>> mapping) {
        this.paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.pdbIdentifiers = new ArrayList<>();
        this.chains = new ArrayList<>();
        prepareMappedIdentifiers(mapping);
    }

    /**
     * Creates a new Iterator based on the context of parsing.
     *
     * @param context The context.
     * @param identifiers The identifiers to parse
     */
    @SuppressWarnings("unchecked")
    StructureContentIterator(Class<?> context, List<?> identifiers) {
        this.paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.pdbIdentifiers = new ArrayList<>();
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

    /**
     * Prepares the identifiers for online parsing.
     *
     * @param identifiers The identifiers to be parsed.
     */
    private void prepareIdentifiers(List<String> identifiers) {
        for (String identifier : identifiers) {
            try {
                this.identifiers.add(new URL(String.format(PDB_FETCH_URL, identifier)));
                this.pdbIdentifiers.add(identifier);
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB for identifier " + identifier, e);
            }
        }
        this.currentURL = this.identifiers.iterator();
        this.pdbIdentifierIterator = this.pdbIdentifiers.iterator();
        this.location = SourceLocation.ONLINE;
    }

    /**
     * Prepares paths to be prepared offline.
     *
     * @param paths The paths to be parsed.
     */
    private void prepareOfflinePaths(List<Path> paths) {
        this.paths = paths;
        this.currentPath = this.paths.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    /**
     * Prepares files to be parsed offline.
     *
     * @param files The files to be parsed.
     */
    private void prepareOfflineFiles(List<File> files) {
        for (File file : files) {
            this.paths.add(file.toPath());
        }
        this.currentPath = this.paths.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    /**
     * Prepares the identifiers to be parsed from the set local pdb.
     *
     * @param identifiers The identifiers to be parsed.
     */
    private void prepareLocalPDB(List<String> identifiers) {
        for (String identifier : identifiers) {
            this.paths.add(assemblePath(identifier));
            this.pdbIdentifiers.add(identifier);
        }
        this.currentPath = this.paths.iterator();
        this.pdbIdentifierIterator = this.pdbIdentifiers.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    /**
     * Prepares the pdb identifiers - chain pairs to be parsed from the set local pdb.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    private void prepareMappedLocalPDB(List<Pair<String>> mapping) {
        for (Pair<String> pair : mapping) {
            this.paths.add(assemblePath(pair.getFirst()));
            this.pdbIdentifiers.add(pair.getFirst());
            this.chains.add(pair.getSecond());
        }
        this.currentPath = this.paths.iterator();
        this.pdbIdentifierIterator = this.pdbIdentifiers.iterator();
        this.chainIdentifierIterator = this.chains.iterator();
        this.location = SourceLocation.OFFLINE;
    }

    /**
     * Prepares the pdb identifiers - chain pairs to be parsed online.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    private void prepareMappedIdentifiers(List<Pair<String>> mapping) {
        for (Pair<String> pair : mapping) {
            try {
                this.identifiers.add(new URL(String.format(PDB_FETCH_URL, pair.getFirst())));
                this.pdbIdentifiers.add(pair.getFirst());
                this.chains.add(pair.getSecond());
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB", e);
            }
        }
        this.currentURL = this.identifiers.iterator();
        this.pdbIdentifierIterator = this.pdbIdentifiers.iterator();
        this.chainIdentifierIterator = this.chains.iterator();
        this.location = SourceLocation.ONLINE;
    }

    /**
     * Creates a path for a structure with the given identifier to the local pbd installations.
     *
     * @param identifier The identifier.
     * @return The assembled path.
     */
    private Path assemblePath(String identifier) {
        return this.localPdb.getLocalPdbPath().resolve(Paths.get(StructureParser.LocalPDB.BASE_PATH + "/"
                + identifier.substring(1, identifier.length() - 1).toLowerCase()
                + "/pdb" + identifier.toLowerCase() + ".ent.gz"));
    }

    /**
     * Extracts the content form a packed file to an input stream.
     *
     * @param path The path to a file to be parsed.
     * @return The files a stream.
     * @throws IOException if the file could not be unpacked or found.
     */
    private InputStream readPacked(Path path) throws IOException {
        return new GZIPInputStream(new FileInputStream(path.toFile()));
    }

    /**
     * Returns the the number of enqueued structures to parse.
     *
     * @return The the number of enqueued structures to parse.
     */
    int getNumberOfQueuedStructures() {
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
    int getNumberOfRemainingStructures() {
        if (this.location == SourceLocation.ONLINE) {
            return this.identifiers.size() - this.progressCounter;
        } else {
            return this.paths.size() - this.progressCounter;
        }
    }

    /**
     * Returns the current pdb identifier.
     *
     * @return the current pdb identifier.
     */
    String getCurrentPdbIdentifier() {
        if (this.currentPdbIdentifier != null) {
            return this.currentPdbIdentifier;
        } else {
            throw new IllegalStateException("Unable to retrieve the PDB Identifier in the current state.");
        }
    }

    /**
     * Returns the current chain identifier.
     *
     * @return The current chain identifier.
     */
    String getCurrentChainIdentifier() {
        if (this.currentChainIdentifier != null) {
            return this.currentChainIdentifier;
        } else {
            throw new IllegalStateException("Unable to retrieve chainIdentifier Identifier in the current state.");
        }
    }

    /**
     * returns the current source.
     *
     * @return The current source.
     */
    String getCurrentSource() {
        return this.currentSource;
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
        if (this.pdbIdentifiers != null && !this.pdbIdentifiers.isEmpty()) {
            this.currentPdbIdentifier = this.pdbIdentifierIterator.next();
            if (this.chains != null) {
                this.currentChainIdentifier = this.chainIdentifierIterator.next();
                logger.debug("Parsing structure {}/{}.", this.currentPdbIdentifier, this.currentChainIdentifier);
            } else {
                logger.debug("Parsing structure {}.", this.currentPdbIdentifier);
            }
        }
        if (this.location == SourceLocation.OFFLINE) {
            try {
                final Path path = this.currentPath.next();
                // remove extension
                this.currentSource = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
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
                URL url = this.currentURL.next();
                this.currentSource = url.getFile();
                return fetchLines(url.openStream());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for URL. The PDB identifier \""
                        + this.currentPdbIdentifier + "\" does not seem to exist", e);
            } finally {
                this.progressCounter++;
            }
        }

    }

    /**
     * Returns a list of lines from the input steam.
     *
     * @param inputStream The input stream.
     * @return The lines of the file.
     * @throws IOException if the file input stream was corrupted.
     */
    private List<String> fetchLines(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return bufferedReader.lines().collect(Collectors.toList());
            }
        }
    }

}
