package de.bioforscher.benchmark.algorithms.superimposition;

import de.bioforscher.chemistry.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.parser.pdb.structures.StructureSources;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.model.LeafIdentifiers;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.chemistry.physical.model.Structure;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A benchmark case that is run to analyze the performance of the Fit3D algorithm.
 *
 * @author fk
 */
// @BenchmarkMode(Mode.AverageTime)
// @OutputTimeUnit(TimeUnit.MICROSECONDS)
// @State(Scope.Benchmark)
// @Timeout(time = Integer.MAX_VALUE)
// @Warmup(iterations = 10)
// @Measurement(iterations = 5)
public class Fit3DAlignmentBenchmark {

    private Structure target;
    private StructuralMotif queryMotif;

    // @Setup
    public void setUp() throws IOException {
        this.target = StructureParser.from(StructureSources.PDB_ONLINE).identifier("1GL0").everything().parse();
        Structure motifContainingStructure = StructureParser.from(StructureSources.PDB_FILE)
                .identifier("D://intellij//singa//benchmark//src//main//resources//1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb")
                .everything().parse();
        this.queryMotif = StructuralMotif.fromLeafs(1, motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    // @Benchmark
    public void benchmarkFit3DAlignment() {
        Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .run();
    }
}
