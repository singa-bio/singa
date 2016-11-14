package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import org.junit.Test;

/**
 * Created by fkaiser on 13.11.16.
 */
public class SubStructureTest {
    @Test
    public void getAtomContainingSubStructures() throws Exception {
        Structure structure = PDBParserService.parsePDBFile(Thread.currentThread()
                                                            .getContextClassLoader()
                                                            .getResource("1pqs.pdb").getPath());
        SubStructure firstsub = structure.getSubstructures().stream().findFirst().get();
        System.out.println(firstsub);
        System.out.println(firstsub.getAtomContainingSubstructures());
        SubStructure secondSub = firstsub.getSubstructures().stream().findFirst().get();
        System.out.println(secondSub);
        System.out.println(secondSub.getAtomContainingSubstructures());
        SubStructure thirdSub = secondSub.getSubstructures().stream().findFirst().get();
        System.out.println(thirdSub);
        System.out.println(thirdSub.getAtomContainingSubstructures());
    }
}