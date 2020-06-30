package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureCollector;
import bio.singa.structure.parser.pdb.structures.StructureParser;

import java.util.List;

/**
 * @author cl
 */
public class PdbIterator<SourceType> implements StructureIterator {

    private final SourceIterator<SourceType, List<String>> sourceIterator;
    private final StructureParser.Reducer reducer;
    private SourceType currentSource;

    public PdbIterator(SourceIterator<SourceType, List<String>> sourceIterator, StructureParser.Reducer reducer) {
        this.sourceIterator = sourceIterator;
        this.reducer = reducer;
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
        return (StructureCollector.parse(sourceIterator.getContent(currentSource), reducer));
    }

    @Override
    public boolean hasChain() {
        return sourceIterator.hasChain();
    }

    @Override
    public String getChain() {
        return sourceIterator.getChain();
    }


}
