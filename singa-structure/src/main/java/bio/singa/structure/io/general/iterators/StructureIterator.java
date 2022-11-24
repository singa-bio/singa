package bio.singa.structure.io.general.iterators;

import bio.singa.core.utility.Pair;
import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.io.cif.MmcifStructureIterator;
import bio.singa.structure.io.cif.RemoteMmCifSourceIterator;
import bio.singa.structure.io.general.*;
import bio.singa.structure.io.general.converters.FileLocationToPathConverter;
import bio.singa.structure.io.general.converters.FileToPathConverter;
import bio.singa.structure.io.general.converters.IdentityConverter;
import bio.singa.structure.io.general.converters.LocalPdbToPathConverter;
import bio.singa.structure.io.general.sources.LocalSourceIterator;
import bio.singa.structure.io.mmtf.RemoteMmtfSourceIterator;
import bio.singa.structure.io.pdb.RemotePdbSourceIterator;
import bio.singa.structure.io.mmtf.MmtfStructureIterator;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.general.LeafSkeleton;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * @author cl
 */
public interface StructureIterator extends Iterator<Structure> {

    class EmptyStructureIterator implements StructureIterator {

        @Override
        public LeafSkeletonFactory getLeafSkeletonFactory() {
            return null;
        }

        @Override
        public void setLeafSkeletonFactory(LeafSkeletonFactory localCcdRepository) {

        }

        @Override
        public StructureParserOptions getOptions() {
            return null;
        }

        @Override
        public void setOptions(StructureParserOptions options) {

        }

        @Override
        public void prepareNext() {

        }

        @Override
        public boolean hasChain() {
            return false;
        }

        @Override
        public int getNumberOfQueuedStructures() {
            return 0;
        }

        @Override
        public int getNumberOfProcessedStructures() {
            return 0;
        }

        @Override
        public int getNumberOfRemainingStructures() {
            return 0;
        }

        @Override
        public List<Structure> parse() {
            return Collections.emptyList();
        }

        @Override
        public String getCurrentPdbIdentifier() {
            return null;
        }

        @Override
        public String getCurrentChainIdentifier() {
            return null;
        }

        @Override
        public String getCurrentSource() {
            return null;
        }

        @Override
        public LeafSkeleton getSkeleton(String threeLetterCode) {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Structure next() {
            throw new IllegalStateException("The iterator contains no sources to iterate.");
        }
    }

    static StructureIterator createFromIdentifiers(List<String> strings, SourceLocation sourceLocation, LocalStructureRepository localStructureRepository) {
        switch (sourceLocation) {
            case ONLINE_MMTF: {
                return new MmtfStructureIterator<>(new RemoteMmtfSourceIterator(strings));
            }
            case ONLINE_PDB: {
                return new PdbStructureIterator<>(new RemotePdbSourceIterator(strings));
            }
            case ONLINE_MMCIF: {
                return new MmcifStructureIterator<>(new RemoteMmCifSourceIterator(strings));
            }
            case OFFLINE_PDB:
            case OFFLINE_MMCIF:
            case OFFLINE_MMTF:
            case OFFLINE_BCIF:{
                return new LocalStructureIterator<>(new LocalSourceIterator<>(strings, LocalPdbToPathConverter.get(localStructureRepository)));
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
            case ONLINE_MMCIF: {
                return new MmcifStructureIterator<>(new RemoteMmCifSourceIterator(chainList, separator));
            }
            case ONLINE_PDB: {
                return new PdbStructureIterator<>(new RemotePdbSourceIterator(chainList, separator));
            }
        }
        throw new StructureParserException("unable to create parser for " + chainList + " and source "+ sourceLocation);
    }

    static StructureIterator createFromChainList(Collection<Pair<String>> chainList, SourceLocation sourceLocation)  {
        switch (sourceLocation) {
            case ONLINE_MMTF: {
                return new MmtfStructureIterator<>(new RemoteMmtfSourceIterator(chainList));
            }
            case ONLINE_MMCIF: {
                return new MmcifStructureIterator<>(new RemoteMmCifSourceIterator(chainList));
            }
            case ONLINE_PDB: {
                return new PdbStructureIterator<>(new RemotePdbSourceIterator(chainList));
            }
        }
        throw new StructureParserException("unable to create parser for " + chainList + " and source "+ sourceLocation);
    }

    static StructureIterator createFromChainList(Path chainList, String separator, LocalStructureRepository localPdb) {
        return new LocalStructureIterator<>(LocalSourceIterator.fromChainList(chainList, separator, LocalPdbToPathConverter.get(localPdb)));
    }

    static StructureIterator createFromChainList(Collection<Pair<String>> chainList, LocalStructureRepository localPdb) {
        return new LocalStructureIterator<>(LocalSourceIterator.fromChainList(chainList, LocalPdbToPathConverter.get(localPdb)));
    }

    static StructureIterator createFromLocalPdb(LocalStructureRepository localPdb) {
        return createFromLocalPdb(localPdb, -1, false);
    }

    static StructureIterator createFromLocalPdb(LocalStructureRepository localPdb, int limit) {
        return createFromLocalPdb(localPdb, limit, false);
    }

    static StructureIterator createFromLocalPdb(LocalStructureRepository localPdb, int limit, boolean shuffle) {
        return new LocalStructureIterator<>(LocalSourceIterator.fromLocalPdb(localPdb, limit, shuffle));
    }

    static StructureIterator createFromFiles(List<File> files, SourceLocation sourceLocation) {
        return new LocalStructureIterator<>(new LocalSourceIterator<>(files, FileToPathConverter.get()));
    }

    static StructureIterator createFromPaths(List<Path> paths, SourceLocation sourceLocation) {
        return new LocalStructureIterator<>(new LocalSourceIterator<>(paths, IdentityConverter.get(Path.class)));
    }

    LeafSkeletonFactory getLeafSkeletonFactory();

    void setLeafSkeletonFactory(LeafSkeletonFactory localCcdRepository);

    StructureParserOptions getOptions();

    void setOptions(StructureParserOptions options);

    void prepareNext();

    boolean hasChain();

    int getNumberOfQueuedStructures();

    int getNumberOfProcessedStructures();

    int getNumberOfRemainingStructures();

    List<Structure> parse();

    String getCurrentPdbIdentifier();

    String getCurrentChainIdentifier();

    String getCurrentSource();

    LeafSkeleton getSkeleton(String threeLetterCode);

}
