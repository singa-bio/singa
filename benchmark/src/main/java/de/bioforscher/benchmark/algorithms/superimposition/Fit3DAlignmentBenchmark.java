package de.bioforscher.benchmark.algorithms.superimposition;

import de.bioforscher.chemistry.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
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
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Timeout(time = Integer.MAX_VALUE)
public class Fit3DAlignmentBenchmark {

    private Structure target;
    private StructuralMotif queryMotif;

    @Setup
    public void setUp() throws IOException {
        this.target = PDBParserService.parseProteinById("1GL0");
        Structure motifContainingStructure = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"));
        this.queryMotif = StructuralMotif.fromLeafs(1, motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableType(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
        System.out.println("setup");
    }

    @Benchmark
    public void benchmarkFit3DAlignment() {
        Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .run();
    }
}
