package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserException;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileLocationToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.LocalPdbToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.implementations.OfflineMmtfIterator;
import bio.singa.structure.parser.pdb.structures.iterators.implementations.OfflinePdbIterator;
import bio.singa.structure.parser.pdb.structures.iterators.implementations.OnlineMmtfIterator;
import bio.singa.structure.parser.pdb.structures.iterators.implementations.OnlinePdbIterator;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * @author cl
 */
public interface StructureIterator extends Iterator<Structure> {

    void prepareNext();

    boolean hasChain();

    int getNumberOfQueuedStructures();

    int getNumberOfRemainingStructures();

    String getCurrentPdbIdentifier();

    String getCurrentChainIdentifier();

    String getCurrentSource();

    static StructureIterator createFromIdentifiers(List<String> strings, StructureParser.Reducer reducer, SourceLocation sourceLocation) {
        switch (sourceLocation) {
            case ONLINE_MMTF: {
                return new PdbIterator<>(new OnlinePdbIterator(strings), reducer);
            }
            case ONLINE_PDB: {
                return new MmtfIterator<>(new OnlineMmtfIterator(strings));
            }
            default: throw new StructureParserException("unable to parse combination of "+sourceLocation+ " and strings.");
        }
    }

    static StructureIterator createFromLocations(List<String> strings, StructureParser.Reducer reducer, SourceLocation sourceLocation) {
        switch (sourceLocation) {
            case OFFLINE_PDB: {
                return new PdbIterator<>(new OfflinePdbIterator<>(strings, FileLocationToPathConverter.get()), reducer);
            }
            case OFFLINE_MMTF: {
                return new MmtfIterator<>(new OfflineMmtfIterator<>(strings, FileLocationToPathConverter.get()));
            }
            default: throw new StructureParserException("unable to parse combination of "+sourceLocation+ " and strings.");
        }
    }

    static StructureIterator createFromIdentifiers(List<String> strings, StructureParser.Reducer reducer, StructureParser.LocalPdb localPdb) {
        SourceLocation sourceLocation = localPdb.getSourceLocation();
        switch (sourceLocation) {
            case OFFLINE_PDB: {
                return new PdbIterator<>(new OfflinePdbIterator<>(strings, LocalPdbToPathConverter.get(localPdb)), reducer);
            }
            case OFFLINE_MMTF: {
                OfflineMmtfIterator<String> stringOfflineMmtfIterator = new OfflineMmtfIterator<>(strings, LocalPdbToPathConverter.get(localPdb));
                return new MmtfIterator<>(stringOfflineMmtfIterator);
            }
            default: throw new StructureParserException("unable to parse combination of "+sourceLocation+ " and strings.");
        }
    }

    static StructureIterator createFromChainList(Path chainList, String separator, StructureParser.Reducer reducer, StructureParser.LocalPdb localPdb) {
        SourceLocation sourceLocation = localPdb.getSourceLocation();
        switch (sourceLocation) {
            case OFFLINE_PDB: {
                return new PdbIterator<>(OfflinePdbIterator.fromChainList(chainList, separator, LocalPdbToPathConverter.get(localPdb)), reducer);
            }
            case OFFLINE_MMTF: {
                return new MmtfIterator<>(OfflineMmtfIterator.fromChainList(chainList, separator, LocalPdbToPathConverter.get(localPdb)));
            }
            case ONLINE_MMTF: {
                return new PdbIterator<>(new OnlinePdbIterator(chainList, separator), reducer);
            }
            case ONLINE_PDB: {
                return new MmtfIterator<>(new OnlineMmtfIterator(chainList, separator));
            }
        }
        throw new StructureParserException("unable to parse from chain list "+chainList);
    }

    static StructureIterator createFromLocalPdb(StructureParser.LocalPdb localPdb, StructureParser.Reducer reducer) {
        SourceLocation sourceLocation = localPdb.getSourceLocation();
        switch (sourceLocation) {
            case OFFLINE_PDB: {
                return new PdbIterator<>(OfflinePdbIterator.fromLocalPdb(localPdb), reducer);
            }
            case OFFLINE_MMTF: {
                return new MmtfIterator<>(OfflineMmtfIterator.fromLocalPdb(localPdb));
            }
        }
        throw new StructureParserException("unable to parse local pdb "+localPdb.getLocalPdbPath());
    }

    static StructureIterator createFromFiles(List<File> files, StructureParser.Reducer reducer, SourceLocation sourceLocation) {
        switch (sourceLocation) {
            case OFFLINE_MMTF: {
                return new PdbIterator<>(new OfflinePdbIterator<>(files, FileToPathConverter.get()), reducer);
            }
            case OFFLINE_PDB: {
                return new MmtfIterator<>(new OfflineMmtfIterator<>(files, FileToPathConverter.get()));
            }
            default:
                throw new StructureParserException("unable to parse combination of "+sourceLocation+ " and files.");
        }
    }

    static StructureIterator createFromPaths(List<Path> paths, StructureParser.Reducer reducer, SourceLocation sourceLocation) {
        switch (sourceLocation) {
            case OFFLINE_MMTF: {
                return new PdbIterator<>(new OfflinePdbIterator<>(paths, IdentityConverter.get(Path.class)), reducer);
            }
            case OFFLINE_PDB: {
                return new MmtfIterator<>(new OfflineMmtfIterator<>(paths, IdentityConverter.get(Path.class)));
            }
            default:
                throw new StructureParserException("unable to parse combination of "+sourceLocation+ " and paths.");
        }
    }

}
