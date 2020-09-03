package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.LocalPDBRepository;
import bio.singa.structure.parser.pdb.structures.SourceLocation;
import bio.singa.structure.parser.pdb.structures.StructureParserException;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileLocationToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.FileToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.LocalPDBToPathConverter;
import bio.singa.structure.parser.pdb.structures.iterators.sources.LocalSourceIterator;
import bio.singa.structure.parser.pdb.structures.iterators.sources.RemoteMmtfSourceIterator;
import bio.singa.structure.parser.pdb.structures.iterators.sources.RemotePDBSourceIterator;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public interface StructureIterator extends Iterator<Structure> {

    static StructureIterator createFromIdentifiers(List<String> strings, SourceLocation sourceLocation, LocalPDBRepository localPdbRepository) {
        switch (sourceLocation) {
            case ONLINE_MMTF: {
                return new MmtfStructureIterator<>(new RemoteMmtfSourceIterator(strings));
            }
            case ONLINE_PDB: {
                return new PDBStructureIterator<>(new RemotePDBSourceIterator(strings));
            }
            case OFFLINE_PDB:
            case OFFLINE_MMTF: {
                return new LocalStructureIterator<>(new LocalSourceIterator<>(strings, LocalPDBToPathConverter.get(localPdbRepository)));
            }
            default:
                throw new StructureParserException("unable to parse combination of " + sourceLocation + " and strings.");
        }
    }

    static StructureIterator createFromLocations(List<String> strings) {
        return new LocalStructureIterator<>(new LocalSourceIterator<>(strings, FileLocationToPathConverter.get()));
    }

    static StructureIterator createFromChainList(Path chainList, String separator, SourceLocation sourceLocation)  {
        switch (sourceLocation) {
            case ONLINE_MMTF: {
                return new MmtfStructureIterator<>(new RemoteMmtfSourceIterator(chainList, separator));
            }
            case ONLINE_PDB: {
                return new PDBStructureIterator<>(new RemotePDBSourceIterator(chainList, separator));
            }
        }

        throw new StructureParserException("unable to create parser for " + chainList + " and source "+ sourceLocation);
    }

    static StructureIterator createFromChainList(Path chainList, String separator, LocalPDBRepository localPdb) {
        return new LocalStructureIterator<>(LocalSourceIterator.fromChainList(chainList, separator, LocalPDBToPathConverter.get(localPdb)));
    }

    static StructureIterator createFromLocalPdb(LocalPDBRepository localPdb) {
        return new LocalStructureIterator<>(LocalSourceIterator.fromLocalPdb(localPdb));
    }

    static StructureIterator createFromFiles(List<File> files, SourceLocation sourceLocation) {
        return new LocalStructureIterator<>(new LocalSourceIterator<>(files, FileToPathConverter.get()));
    }

    static StructureIterator createFromPaths(List<Path> paths, SourceLocation sourceLocation) {
        return new LocalStructureIterator<>(new LocalSourceIterator<>(paths, IdentityConverter.get(Path.class)));
    }

    void prepareNext();

    void skip();

    boolean hasChain();

    int getNumberOfQueuedStructures();

    int getNumberOfRemainingStructures();

    List<Structure> parse();

    String getCurrentPdbIdentifier();

    String getCurrentChainIdentifier();

    String getCurrentSource();

    StructureReducer getReducer();

    void setReducer(StructureReducer reducer);

    Map<String, LeafSkeleton> getSkeletons();

}
