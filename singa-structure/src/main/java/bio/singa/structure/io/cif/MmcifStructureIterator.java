package bio.singa.structure.io.cif;

import bio.singa.structure.io.general.iterators.AbstractStructureIterator;
import bio.singa.structure.io.general.sources.SourceIterator;
import bio.singa.structure.model.cif.CifConverter;
import bio.singa.structure.model.interfaces.Structure;
import org.rcsb.cif.model.CifFile;
import org.rcsb.cif.schema.StandardSchemata;

public class MmcifStructureIterator<SourceType> extends AbstractStructureIterator<SourceType, CifFile> {

    public MmcifStructureIterator(SourceIterator<SourceType, CifFile> sourceIterator) {
        super(sourceIterator);
    }

    @Override
    public Structure next() {
        return CifConverter.convert((sourceIterator.getContent(currentSource)).as(StandardSchemata.MMCIF));
    }

}
