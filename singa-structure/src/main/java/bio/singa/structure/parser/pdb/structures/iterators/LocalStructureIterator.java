package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.StructureCollector;
import bio.singa.structure.parser.pdb.structures.iterators.sources.LocalSourceIterator;

import java.util.List;

/**
 * @author cl
 */
public class LocalStructureIterator<SourceType> extends AbstractStructureIterator<SourceType, Object> {

    public LocalStructureIterator(LocalSourceIterator<SourceType> offlineIterator) {
        super(offlineIterator);
    }

    @Override
    public Structure next() {
        Object content = sourceIterator.getContent(currentSource);
        if (content instanceof byte[]) {
            byte[] bytes = (byte[]) content;
            MmtfStructure mmtfStructure = new MmtfStructure(bytes, false);
            if (reducer.isReducingChains() || reducer.isReducingModels()) {
                MmtfReducer.reduceMMTFStructure(mmtfStructure, reducer);
            }
            return mmtfStructure;
        } else if (content instanceof List) {
            List<String> strings = ((List<String>) content);
            return StructureCollector.parse(strings, this);
        }
        throw new IllegalStateException("Expected List of Strings or byte array but recived " + content.getClass());
    }
}
