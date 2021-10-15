package bio.singa.structure.io.general.iterators;

import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.io.general.LocalCcdRepository;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.sources.SourceIterator;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.general.LeafSkeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public abstract class AbstractStructureIterator<SourceType, TargetType> implements StructureIterator {

    protected final SourceIterator<SourceType, TargetType> sourceIterator;
    protected int counter = 0;
    protected SourceType currentSource;

    private LeafSkeletonFactory leafSkeletonFactory;
    private StructureParserOptions options;

    public AbstractStructureIterator(SourceIterator<SourceType, TargetType> sourceIterator) {
        this.sourceIterator = sourceIterator;
        options = new StructureParserOptions();
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    @Override
    public void prepareNext() {
        currentSource = sourceIterator.next();
        counter++;
    }

    @Override
    public LeafSkeletonFactory getLeafSkeletonFactory() {
        return leafSkeletonFactory;
    }

    @Override
    public void setLeafSkeletonFactory(LeafSkeletonFactory leafSkeletonFactory) {
        this.leafSkeletonFactory = leafSkeletonFactory;
    }

    @Override
    public StructureParserOptions getOptions() {
        return options;
    }

    @Override
    public void setOptions(StructureParserOptions options) {
        this.options = options;
    }

    @Override
    public boolean hasChain() {
        return sourceIterator.hasChain();
    }

    @Override
    public int getNumberOfQueuedStructures() {
        return sourceIterator.getSources().size();
    }

    @Override
    public int getNumberOfProcessedStructures() {
        return counter;
    }

    @Override
    public int getNumberOfRemainingStructures() {
        return getNumberOfQueuedStructures() - counter;
    }

    @Override
    public String getCurrentPdbIdentifier() {
        return PDBIdentifier.extractLast(getCurrentSource());
    }

    @Override
    public String getCurrentChainIdentifier() {
        return sourceIterator.getChain();
    }

    @Override
    public String getCurrentSource() {
        return currentSource.toString();
    }

    @Override
    public LeafSkeleton getSkeleton(String threeLetterCode) {
        return leafSkeletonFactory.getLeafSkeleton(threeLetterCode);
    }

    @Override
    public List<Structure> parse() {
        List<Structure> structures = new ArrayList<>();
        while (hasNext()) {
            prepareNext();
            structures.add(next());
        }
        return structures;
    }

}
