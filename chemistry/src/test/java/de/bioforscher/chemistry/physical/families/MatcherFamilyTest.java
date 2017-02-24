package de.bioforscher.chemistry.physical.families;

import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author fk
 */
public class MatcherFamilyTest {

    @Test
    public void shouldReadSubstitutionMatrices() throws URISyntaxException, IOException {

        List<String> stringContent = Files.readAllLines(Paths.get(Thread.currentThread()
                .getContextClassLoader()
                .getResource("physical/families/substitution/matrices/substituion_matrics_MCLachlan_1972_relative_frequencies_of_substitutions.csv")
                .toURI()));

        // collect labels
        List<StructuralFamily> labels = new ArrayList<>();
        for (String line : stringContent) {
            String[] splittedLine = line.split(",");
            if (splittedLine[0].isEmpty()) {
                continue;
            }
            Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(splittedLine[0]);
            labels.add(aminoAcidFamily.get());
        }

        double[][] temporaryMatrix = new double[20][20];
        for (int i = 1; i < stringContent.size(); i++) {
            String currentRow = stringContent.get(i);
            String[] splittedRow = currentRow.split(",");
            for (int j = 1; j < splittedRow.length; j++) {
                System.out.println(labels.get(i-1) + splittedRow[j]);
                temporaryMatrix[i-1][j-1] = Double.valueOf(splittedRow[j]);
                temporaryMatrix[j-1][i-1] = Double.valueOf(splittedRow[j]);
            }
        }
        LabeledSymmetricMatrix<StructuralFamily> matrix = new LabeledSymmetricMatrix<>(temporaryMatrix);
        matrix.setRowLabels(labels);
        System.out.println(matrix.getStringRepresentation());

        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/main/resources/physical/families/substitution/matrices/mclachan_1972_relative.mat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(matrix);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }
}