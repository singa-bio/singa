package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;

/**
 * @author cl
 */
public class MmtfIterator<SourceType> implements StructureIterator {

    private final SourceIterator<SourceType, byte[]> sourceIterator;
    private int counter = 0;
    private SourceType currentSource;

    public MmtfIterator(SourceIterator<SourceType, byte[]> sourceIterator) {
        this.sourceIterator = sourceIterator;
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    public void prepareNext() {
        currentSource = sourceIterator.next();
    }

    @Override
    public Structure next() {
        MmtfStructure structure = new MmtfStructure(sourceIterator.getContent(currentSource), false);
        counter++;
        return structure;
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
    public String getChain() {
        return sourceIterator.getChain();
    }

    @Override
    public int getNumberOfQueuedStructures() {
        return sourceIterator.getSources().size();
    }

    @Override
    public int getNumberOfRemainingStructures() {
        return getNumberOfQueuedStructures()-counter;
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
}
