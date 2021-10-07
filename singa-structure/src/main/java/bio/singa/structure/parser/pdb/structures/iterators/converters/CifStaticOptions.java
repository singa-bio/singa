package bio.singa.structure.parser.pdb.structures.iterators.converters;

import org.rcsb.cif.CifOptions;

class CifStaticOptions {

    static CifOptions BCIF_PLAIN = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.BCIF_PLAIN)
                    .build();

    static CifOptions CIF_PLAIN = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.CIF_PLAIN)
            .build();

    static CifOptions BCIF_GZIPPED = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.BCIF_GZIPPED)
            .build();

    static CifOptions CIF_GZIPPED = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.CIF_GZIPPED)
            .build();

}
