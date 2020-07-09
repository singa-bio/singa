package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureCollector;
import bio.singa.structure.parser.pdb.structures.iterators.sources.SourceIterator;

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
        return (StructureCollector.parse(sourceIterator.getContent(currentSource), this));
    }

}
