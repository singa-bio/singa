package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.core.utility.Pair;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static bio.singa.structure.parser.pdb.structures.SourceLocation.*;

/**
 * This iterator administers each structure that should be parsed and its origin.
 *
 * @author cl
 */
public class StructureContentIterator implements Iterator<List<String>> {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(StructureContentIterator.class);

    /**
     * The URL the where pdb files are parsed from.
     */
    private static final String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";
    /**
     * The pdb identifiers that are to be parsed, if they can be inferred.
     */
    private final List<String> pdbIdentifiers;
    /**
     * The local pdb, if there is any.
     */
    private StructureParser.LocalPdb localPdb;
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
    private Iterator<Path> pathIterator;
    private Path currentPath;
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
    public StructureContentIterator(String identifier, SourceLocation sourceLocation) {
        this(String.class, Collections.singletonList(identifier), sourceLocation);
    }

    /**
     * Creates a new structure content iterator for a single path.
     *
     * @param path The path of a file that is to be parsed.
     */
    public StructureContentIterator(Path path) {
        this(Path.class, Collections.singletonList(path), OFFLINE_PDB);
    }

    /**
     * Creates a new structure content iterator for a single file.
     *
     * @param file The  file that is to be parsed.
     */
    public StructureContentIterator(File file) {
        this(File.class, Collections.singletonList(file), OFFLINE_PDB);
    }

    /**
     * Creates a new structure content iterator to parse a specific pdb file from a local pbd installation.
     *
     * @param localPdb The pdb installation.
     * @param identifier The identifier that is to be parsed.
     */
    public StructureContentIterator(StructureParser.LocalPdb localPdb, String identifier) {
        this.localPdb = localPdb;
        paths = new ArrayList<>();
        pdbIdentifiers = new ArrayList<>();
        prepareLocalPDB(Collections.singletonList(identifier));
    }

    /**
     * Creates a new structure content iterator to parse multiple pdb files from a local pdb installation.
     *
     * @param localPdb The pdb installation.
     * @param identifiers The identifiers that are to be parsed.
     */
    public StructureContentIterator(StructureParser.LocalPdb localPdb, List<String> identifiers) {
        this.localPdb = localPdb;
        paths = new ArrayList<>();
        pdbIdentifiers = new ArrayList<>();
        prepareLocalPDB(identifiers);
    }

    /**
     * Creates a new structure content iterator to parse specific cains of proteins from a local pdb installation.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     * @param localPDB The pdb installation.
     */
    public StructureContentIterator(List<Pair<String>> mapping, StructureParser.LocalPdb localPDB) {
        // sorry for the argument switch to work around erasure :(
        localPdb = localPDB;
        paths = new ArrayList<>();
        pdbIdentifiers = new ArrayList<>();
        chains = new ArrayList<>();
        prepareMappedLocalPDB(mapping);
    }

    /**
     * Creates a new structure content iterator to parse specific cains of proteins online.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    public StructureContentIterator(List<Pair<String>> mapping, SourceLocation sourceLocation) {
        paths = new ArrayList<>();
        identifiers = new ArrayList<>();
        pdbIdentifiers = new ArrayList<>();
        chains = new ArrayList<>();
        prepareMappedIdentifiers(mapping, sourceLocation);
    }

    /**
     * Creates a new Iterator based on the context of parsing.
     *
     * @param context The context.
     * @param identifiers The identifiers to parse
     */
    @SuppressWarnings("unchecked")
    public StructureContentIterator(Class<?> context, List<?> identifiers, SourceLocation sourceLocation) {
        paths = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        pdbIdentifiers = new ArrayList<>();
        progressCounter = 0;

        if (context.equals(String.class)) {
            // pdbIdentifiers for pdb online
            prepareIdentifiers((List<String>) identifiers, sourceLocation);
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
    private void prepareIdentifiers(List<String> identifiers, SourceLocation sourceLocation) {
        for (String identifier : identifiers) {
            try {
                if (sourceLocation == ONLINE_PDB) {
                    this.identifiers.add(new URL(String.format(PDB_FETCH_URL, identifier)));
                }
                pdbIdentifiers.add(identifier);
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB for identifier " + identifier, e);
            }
        }
        currentURL = this.identifiers.iterator();
        pdbIdentifierIterator = pdbIdentifiers.iterator();
        location = sourceLocation;
    }

    /**
     * Prepares paths to be prepared offline.
     *
     * @param paths The paths to be parsed.
     */
    private void prepareOfflinePaths(List<Path> paths) {
        this.paths = paths;
        pathIterator = this.paths.iterator();
        currentPath = pathIterator.next();
        location = OFFLINE_PDB;
    }

    /**
     * Prepares files to be parsed offline.
     *
     * @param files The files to be parsed.
     */
    private void prepareOfflineFiles(List<File> files) {
        for (File file : files) {
            paths.add(file.toPath());
        }
        pathIterator = paths.iterator();
        currentPath = pathIterator.next();
        location = OFFLINE_PDB;
    }

    /**
     * Prepares the identifiers to be parsed from the set local pdb.
     *
     * @param identifiers The identifiers to be parsed.
     */
    private void prepareLocalPDB(List<String> identifiers) {
        for (String identifier : identifiers) {
            paths.add(assemblePath(identifier));
            pdbIdentifiers.add(identifier);
        }
        pathIterator = paths.iterator();
        currentPath = pathIterator.next();
        pdbIdentifierIterator = pdbIdentifiers.iterator();
        location = OFFLINE_PDB;
    }

    /**
     * Prepares the pdb identifiers - chain pairs to be parsed from the set local pdb.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    private void prepareMappedLocalPDB(List<Pair<String>> mapping) {
        for (Pair<String> pair : mapping) {
            paths.add(assemblePath(pair.getFirst()));
            pdbIdentifiers.add(pair.getFirst());
            chains.add(pair.getSecond());
        }
        pathIterator = paths.iterator();
        pdbIdentifierIterator = pdbIdentifiers.iterator();
        chainIdentifierIterator = chains.iterator();
        location = OFFLINE_PDB;
    }

    /**
     * Prepares the pdb identifiers - chain pairs to be parsed online.
     *
     * @param mapping The pdb identifier - chain identifier mapping.
     */
    private void prepareMappedIdentifiers(List<Pair<String>> mapping, SourceLocation sourceLocation) {
        for (Pair<String> pair : mapping) {
            try {
                if (sourceLocation == ONLINE_PDB) {
                    identifiers.add(new URL(String.format(PDB_FETCH_URL, pair.getFirst())));
                }
                pdbIdentifiers.add(pair.getFirst());
                chains.add(pair.getSecond());
            } catch (MalformedURLException e) {
                throw new UncheckedIOException("Malformed URL to PDB", e);
            }
        }
        currentURL = identifiers.iterator();
        pdbIdentifierIterator = pdbIdentifiers.iterator();
        chainIdentifierIterator = chains.iterator();
        location = sourceLocation;
    }

    /**
     * Creates a path for a structure with the given identifier to the local pbd installations.
     *
     * @param identifier The identifier.
     * @return The assembled path.
     */
    private Path assemblePath(String identifier) {
        return localPdb.getPathForPdbIdentifier(identifier);
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
    public int getNumberOfQueuedStructures() {
        if (location == ONLINE_PDB || location == ONLINE_MMTF) {
            return pdbIdentifiers.size();
        } else {
            return paths.size();
        }
    }

    /**
     * Returns the the number of structures that still have to be parsed.
     *
     * @return The the number of structures still to be parsed.
     */
    public int getNumberOfRemainingStructures() {
        if (location == ONLINE_PDB || location == ONLINE_MMTF) {
            return pdbIdentifiers.size() - progressCounter;
        } else {
            return paths.size() - progressCounter;
        }
    }

    /**
     * Returns the current pdb identifier.
     *
     * @return the current pdb identifier.
     */
    public String getCurrentPdbIdentifier() {
        if (currentPdbIdentifier != null) {
            return currentPdbIdentifier;
        } else {
            throw new IllegalStateException("Unable to retrieve the PDB Identifier in the current state.");
        }
    }

    /**
     * Returns the current chain identifier.
     *
     * @return The current chain identifier.
     */
    public String getCurrentChainIdentifier() {
        if (currentChainIdentifier != null) {
            return currentChainIdentifier;
        } else {
            throw new IllegalStateException("Unable to retrieve chain Identifier in the current state.");
        }
    }

    /**
     * returns the current source.
     *
     * @return The current source.
     */
    public String getCurrentSource() {
        return currentSource;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    @Override
    public boolean hasNext() {
        switch (location) {
            case ONLINE_PDB:
                return currentURL.hasNext();
            case ONLINE_MMTF:
                return pdbIdentifierIterator.hasNext();
            case OFFLINE_PDB:
                return currentPath != null;
            default:
                return pathIterator.hasNext();
        }
    }

    @Override
    public List<String> next() {
        if (pdbIdentifiers != null && !pdbIdentifiers.isEmpty()) {
            currentPdbIdentifier = pdbIdentifierIterator.next();
            if (chains != null) {
                currentChainIdentifier = chainIdentifierIterator.next();
                logger.debug("Parsing structure {}/{}.", currentPdbIdentifier, currentChainIdentifier);
            } else {
                logger.debug("Parsing structure {}.", currentPdbIdentifier);
            }
        }
        switch (location) {
            case OFFLINE_PDB:
                try {
                    // remove extension
                    currentSource = currentPath.getFileName().toString().replaceFirst("[.][^.]+$", "");
                    List<String> strings;
                    if (currentPath.toString().endsWith(".ent.gz")) {
                        strings = fetchLines(readPacked(currentPath));
                    } else if (currentPath.toString().endsWith(".mmtf.gz")) {
                        strings = Collections.singletonList(currentPath.toString());
                    } else {
                        strings = fetchLines(Files.newInputStream(currentPath));
                    }
                    if (pathIterator.hasNext()) {
                        currentPath = pathIterator.next();
                    } else {
                        currentPath = null;
                    }
                    return strings;
                } catch (IOException e) {
                    throw new UncheckedIOException("Could not open input stream for path.", e);
                } finally {
                    progressCounter++;
                }

            case ONLINE_PDB:
                try {
                    URL url = currentURL.next();
                    currentSource = url.getFile();
                    return fetchLines(url.openStream());
                } catch (IOException e) {
                    throw new UncheckedIOException("Could not open input stream for URL. The PDB identifier \""
                            + currentPdbIdentifier + "\" does not seem to exist", e);
                } finally {
                    progressCounter++;
                }
            default:
                progressCounter++;
                return Collections.singletonList(currentPdbIdentifier);
        }

    }

    public void skip() {
        currentPath = pathIterator.next();
        progressCounter++;
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
