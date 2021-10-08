package bio.singa.structure.io.general.iterators;

import bio.singa.structure.io.general.sources.SourceIterator;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.pdb.PdbStructureParser;

import java.util.List;

/**
 * @author cl
 */
public class PdbStructureIterator<SourceType> extends AbstractStructureIterator<SourceType, List<String>> {

    public PdbStructureIterator(SourceIterator<SourceType, List<String>> sourceIterator) {
        super(sourceIterator);
    }

    @Override
    public Structure next() {
        return (PdbStructureParser.parse(sourceIterator.getContent(currentSource), this));
    }

}
