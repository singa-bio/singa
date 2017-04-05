package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.algorithms.superimposition.XieScore;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A parallel version of the {@link Fit3DAlignment} for substructure search in a set of target structures.
 *
 * @author fk
 */
public class Fit3DAlignmentBatch implements Fit3D {

    private static final Logger logger = LoggerFactory.getLogger(Fit3DAlignmentBatch.class);
    private final StructuralMotif queryMotif;
    private final List<String> targetStructures;
    private final Predicate<Atom> atomFilter;
    private final RepresentationScheme representationScheme;
    private final int parallelism;
    private final double rmsdCutoff;
    private final double distanceTolerance;
    private final ExecutorService executorService;
    private final StructureParser.MultiParser multiParser;
    private TreeMap<Double, SubstructureSuperimposition> allMatches;

    Fit3DAlignmentBatch(Fit3DBuilder.Builder builder) {
        this.queryMotif = builder.queryMotif;
        this.targetStructures = builder.targetStructures;
        this.multiParser = StructureParser.local()
                .fileLocations(builder.targetStructures)
                .everything();
        this.parallelism = builder.parallelism;
        this.executorService = Executors.newWorkStealingPool(this.parallelism);
        this.atomFilter = builder.atomFilter;
        this.representationScheme = builder.representationScheme;
        this.rmsdCutoff = builder.rmsdCutoff;
        this.distanceTolerance = builder.distanceTolerance;
        logger.info("Fit3D alignment batch initialized with {} target structures", this.targetStructures.size());
        computeAlignments();
        logger.info("found {} matches in {} target structures", this.allMatches.size(), this.targetStructures.size());
    }

    /**
     * Creates jobs and executes them in parallel.
     */
    private void computeAlignments() {
        List<Fit3DCalculator> jobs = this.targetStructures.stream()
                .map(targetStructure -> new Fit3DCalculator())
                .collect(Collectors.toList());
        try {
            this.allMatches = this.executorService.invokeAll(jobs).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(TreeMap::new, Map::putAll, Map::putAll);
        } catch (InterruptedException e) {
            logger.error("Ft3D parallel execution failed", e);
        }
    }

    /**
     * Returns all matches of this Fit3D batch calculation.
     *
     * @return The matches in all target structures.
     */
    @Override
    public TreeMap<Double, SubstructureSuperimposition> getMatches() {
        return this.allMatches;
    }

    /**
     * Returns the fraction of aligned residues, which is always 1.0 for this kind of alignment.
     */
    @Override
    public double getFraction() {
        return 1.0;
    }

    @Override
    public XieScore getXieScore() {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Internal class for parallel calculation of {@link Fit3DAlignment}s.
     */
    private class Fit3DCalculator implements Callable<TreeMap<Double, SubstructureSuperimposition>> {

        private String targetStructure;

        @Override
        public TreeMap<Double, SubstructureSuperimposition> call() throws Exception {
            // FIXME here we are dealing only with the first model
            Fit3D fit3d;
            if (Fit3DAlignmentBatch.this.multiParser.hasNext()) {
                BranchSubstructure<?> target = Fit3DAlignmentBatch.this.multiParser.next().getAllModels().get(0);
                logger.info("computing Fit3D alignment against {}", target);
                // create Fit3DAlignment and decide between AtomFilter or RepresentationScheme
                if (Fit3DAlignmentBatch.this.representationScheme == null) {
                    fit3d = Fit3DBuilder.create()
                            .query(Fit3DAlignmentBatch.this.queryMotif)
                            .target(target)
                            .atomFilter(Fit3DAlignmentBatch.this.atomFilter)
                            .rmsdCutoff(Fit3DAlignmentBatch.this.rmsdCutoff)
                            .distanceTolerance(Fit3DAlignmentBatch.this.distanceTolerance)
                            .run();
                } else {
                    fit3d = Fit3DBuilder.create()
                            .query(Fit3DAlignmentBatch.this.queryMotif)
                            .target(target)
                            .representationScheme(Fit3DAlignmentBatch.this.representationScheme.getType())
                            .rmsdCutoff(Fit3DAlignmentBatch.this.rmsdCutoff)
                            .distanceTolerance(Fit3DAlignmentBatch.this.distanceTolerance)
                            .run();
                }
                return fit3d.getMatches();
            }
            return null;
        }
    }
}
