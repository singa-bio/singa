package bio.singa.structure.io.mmtf;

import bio.singa.structure.io.general.iterators.AbstractStructureIterator;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.io.general.sources.SourceIterator;

/**
 * @author cl
 */
public class MmtfStructureIterator<SourceType> extends AbstractStructureIterator<SourceType, byte[]> {

    public MmtfStructureIterator(SourceIterator<SourceType, byte[]> sourceIterator) {
        super(sourceIterator);
    }

    @Override
    public Structure next() {
        return new MmtfStructure(sourceIterator.getContent(currentSource), true);
    }

}
