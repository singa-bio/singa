package bio.singa.structure.io.general.iterators;

import bio.singa.structure.io.general.sources.LocalSourceIterator;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.model.cif.CifConverter;
import bio.singa.structure.io.pdb.PdbStructureParser;
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
            return new MmtfStructure(bytes, false);
        } else if (content instanceof List) {
            List<String> strings = ((List<String>) content);
            return PdbStructureParser.parse(strings, this);
        } else if (content instanceof CifFile) {
            return CifConverter.convert(((CifFile) content).as(StandardSchemata.MMCIF), getLeafSkeletonFactory());
        }
        throw new IllegalStateException("unable to read structure from " + content.getClass());
    }



}
