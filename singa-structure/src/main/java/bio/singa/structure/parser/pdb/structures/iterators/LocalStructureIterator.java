package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.cif.CifConverter;
import bio.singa.structure.parser.pdb.structures.StructureCollector;
import bio.singa.structure.parser.pdb.structures.iterators.sources.LocalSourceIterator;
import org.rcsb.cif.model.CifFile;
import org.rcsb.cif.schema.StandardSchemata;

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
        } else if (content instanceof CifFile) {
            return CifConverter.convert(((CifFile) content).as(StandardSchemata.MMCIF));
        }
        throw new IllegalStateException("unable to read structure from " + content.getClass());
    }



}
