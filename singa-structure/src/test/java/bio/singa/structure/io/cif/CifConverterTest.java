package bio.singa.structure.io.cif;

import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.iterators.StructureIterator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

class CifConverterTest {

    @Test
    void shouldParseCifFiles() throws IOException {

        List<Path> cifFiles = Files.walk(Paths.get("/home/cl/Downloads/test_af_data/"))
                .filter(path -> path.toString().endsWith(".cif.gz"))
                .collect(Collectors.toList());

        StructureIterator structureIterator = StructureParser.local()
                .paths(cifFiles)
                .everything();

        while (structureIterator.hasNext()) {
            structureIterator.prepareNext();
            System.out.println(structureIterator.next().getStructureIdentifier());
        }

    }

    @Test
    void shouldParsePdbFiles() throws IOException {

        List<Path> pdbFiles = Files.walk(Paths.get("/home/cl/Downloads/test_af_data/"))
                .filter(path -> path.toString().endsWith(".pdb.gz"))
                .collect(Collectors.toList());

        StructureIterator structureIterator = StructureParser.local()
                .paths(pdbFiles)
                .settings(StructureParserOptions.Setting.GET_TITLE_FROM_FILENAME)
                .everything();

        while (structureIterator.hasNext()) {
            structureIterator.prepareNext();
            System.out.println(structureIterator.next().getTitle());
        }

    }


    // test alignment methods

}