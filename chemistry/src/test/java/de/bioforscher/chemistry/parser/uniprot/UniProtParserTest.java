package de.bioforscher.chemistry.parser.uniprot;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.chemistry.descriptive.annotations.AnnotationType;
import de.bioforscher.chemistry.parser.*;
import de.bioforscher.core.biology.Organism;
import de.bioforscher.core.biology.Taxon;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Christoph on 19.04.2016.
 */
public class UniProtParserTest {

    //@Test
    public void testWholeSwissProt() {
        String accession_number_list = Thread.currentThread().getContextClassLoader().getResource("uniprot_all_accession_numbers.list").toString();
        Path path = Paths.get(accession_number_list.substring(6));

        try {
            int totalNumber = (int)Files.lines(path).count();
            final int[] i = {0};
            Files.lines(path).forEach( accession -> {
                if (i[0] % (10) == 0) {
                    System.out.println(i[0] + " of " + totalNumber + " parsed");
                }
                Enzyme entity = UniProtParserService.parse(accession);
                assertTrue(!entity.getName().equals(""));
                i[0]++;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testExemplaryUniProtAccession() {
        Enzyme entity = UniProtParserService.parse("P12345");
        System.out.println("Primary name:");
        System.out.println(entity.getName());
        System.out.println();

        System.out.println("Additional Names:");
        List<String> names = entity.getAdditionalNames();
        names.forEach(System.out::println);
        System.out.println();

        System.out.println("Sequence");
        for (String sequence : entity.getAllAminoAcidSequences()) {
            System.out.println(sequence);
            System.out.println();
        }

        System.out.println("Organism");
        for (Organism organism : entity.getAllOrganisms()) {
            System.out.println(organism.getName());
            System.out.println(organism.getCommonName());
            System.out.println("TaxID: " + organism.getIdentifier());
            System.out.println(organism.getLineage()
                    .stream()
                    .map(Taxon::getName)
                    .collect(Collectors.joining(" - " + "")));
            System.out.println();
        }

        System.out.println("Notes");
        for (Annotation note: entity.getAnnotationsOfType(AnnotationType.NOTE)) {
            System.out.println(note.getDescription()+": "+note.getContent());
        }
    }

}