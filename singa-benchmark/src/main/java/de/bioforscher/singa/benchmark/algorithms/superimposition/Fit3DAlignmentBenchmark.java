package de.bioforscher.singa.benchmark.algorithms.superimposition;

import de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifiers;
import de.bioforscher.singa.chemistry.physical.model.Structure;
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
        this.target = StructureParser.online()
                .pdbIdentifier("1GL0")
                .parse();
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb").getFile())
                .parse();
        this.queryMotif = StructuralMotif.fromLeaves(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Benchmark
    public void benchmarkFit3DAlignment() {
        Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .run();
    }
}
