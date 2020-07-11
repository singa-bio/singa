package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.iterators.sources.SourceIterator;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;

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
    protected StructureReducer reducer;

    protected Map<String, LeafSkeleton> skeletons;

    public AbstractStructureIterator(SourceIterator<SourceType, TargetType> sourceIterator) {
        this.sourceIterator = sourceIterator;
        reducer = new StructureReducer();
        skeletons = new HashMap<>();
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    public void prepareNext() {
        currentSource = sourceIterator.next();
    }

    public void skip() {
        prepareNext();
        counter++;
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
    public int getNumberOfRemainingStructures() {
        return getNumberOfQueuedStructures() - counter;
    }

    @Override
    public String getCurrentPdbIdentifier() {
        return PDBIdentifier.extractFirst(getCurrentSource());
    }

    @Override
    public String getCurrentChainIdentifier() {
        return sourceIterator.getChain();
    }

    @Override
    public String getCurrentSource() {
        return currentSource.toString();
    }

    public Map<String, LeafSkeleton> getSkeletons() {
        return skeletons;
    }

    @Override
    public StructureReducer getReducer() {
        return reducer;
    }

    @Override
    public void setReducer(StructureReducer reducer) {
        this.reducer = reducer;
    }

    public List<Structure> parse() {
        List<Structure> structures = new ArrayList<>();
        while (hasNext()) {
            prepareNext();
            structures.add(next());
        }
        return structures;
    }

}
