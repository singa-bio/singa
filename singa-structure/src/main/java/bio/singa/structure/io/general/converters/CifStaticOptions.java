package bio.singa.structure.io.general.converters;

import org.rcsb.cif.CifOptions;

public class CifStaticOptions {

    public static CifOptions BCIF_PLAIN = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.BCIF_PLAIN)
                    .build();

    public static CifOptions CIF_PLAIN = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.CIF_PLAIN)
            .build();

    public static CifOptions BCIF_GZIPPED = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.BCIF_GZIPPED)
            .build();

    public static CifOptions CIF_GZIPPED = CifOptions.builder()
            .fileFormatHint(CifOptions.CifOptionsBuilder.FileFormat.CIF_GZIPPED)
            .build();

}
