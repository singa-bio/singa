package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.iterators.sources.SourceIterator;

/**
 * @author cl
 */
public class MmtfStructureIterator<SourceType> extends AbstractStructureIterator<SourceType, byte[]> {

    public MmtfStructureIterator(SourceIterator<SourceType, byte[]> sourceIterator) {
        super(sourceIterator);
    }

    @Override
    public Structure next() {
        MmtfStructure structure = new MmtfStructure(sourceIterator.getContent(currentSource), true);
        if (reducer.isReducingChains() || reducer.isReducingModels()) {
            MmtfReducer.reduceMMTFStructure(structure, reducer);
        }
        counter++;
        return structure;
    }

}
