package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimpositionException;
import de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.statistics.StatisticalModel;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserException;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.chemistry.physical.model.Structures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final Predicate<Atom> atomFilter;
    private final RepresentationScheme representationScheme;
    private final int parallelism;
    private final double rmsdCutoff;
    private final double distanceTolerance;
    private final ExecutorService executorService;
    private final StructureParser.MultiParser multiParser;
    private final boolean skipAlphaCarbonTargets;
    private final boolean skipBackboneTargets;
    private final StatisticalModel statisticalModel;
    private List<Fit3DMatch> allMatches;

    Fit3DAlignmentBatch(Fit3DBuilder.Builder builder) {
        this.queryMotif = builder.queryMotif;
        this.multiParser = builder.multiParser;
        this.parallelism = builder.parallelism;
        this.skipAlphaCarbonTargets = builder.skipAlphaCarbonTargets;
        this.skipBackboneTargets = builder.skipBackboneTargets;
        this.executorService = Executors.newWorkStealingPool(this.parallelism);
        this.atomFilter = builder.atomFilter;
        this.representationScheme = builder.representationScheme;
        this.rmsdCutoff = builder.rmsdCutoff;
        this.distanceTolerance = builder.distanceTolerance;
        this.statisticalModel = builder.statisticalModel;
        logger.info("Fit3D alignment batch initialized with {} target structures", this.multiParser.getNumberOfQueuedStructures());
        computeAlignments();
        logger.info("found {} matches in {} target structures", this.allMatches.size(), this.multiParser.getNumberOfQueuedStructures());
    }

    /**
     * Creates jobs and executes them in parallel.
     */
    private void computeAlignments() {

        // create the exact number of jobs
        List<Fit3DCalculator> jobs = new ArrayList<>();
        for (int i = 0; i < this.multiParser.getNumberOfQueuedStructures(); i++) {
            jobs.add(new Fit3DCalculator());
        }

        try {
            this.allMatches = this.executorService.invokeAll(jobs).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("Ft3D parallel execution failed", e);
                            throw new IllegalStateException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            logger.error("Ft3D parallel execution failed", e);
        }

        Collections.sort(this.allMatches);

        // calculate statistics
        if (this.statisticalModel != null) {
            try {
                this.statisticalModel.calculatePvalues(this.allMatches);
            } catch (Exception e) {
                logger.warn("failed to calculate p-values", e);
            }
        }
    }

    /**
     * Returns all matches of this Fit3D batch calculation.
     *
     * @return The matches in all target structures.
     */
    @Override
    public List<Fit3DMatch> getMatches() {
        return this.allMatches;
    }

    /**
     * Returns the fraction of aligned residues, which is always 1.0 for this kind of alignment.
     */
    @Override
    public double getFraction() {
        return 1.0;
    }

    /**
     * Internal class for parallel calculation of {@link Fit3DAlignment}s.
     */
    private class Fit3DCalculator implements Callable<List<Fit3DMatch>> {

        @Override
        public List<Fit3DMatch> call() throws Exception {
            // FIXME here we are dealing only with the first model
            Fit3D fit3d;
            if (Fit3DAlignmentBatch.this.multiParser.hasNext()) {
                try {
                    Structure structure = Fit3DAlignmentBatch.this.multiParser.next();
                    if (Fit3DAlignmentBatch.this.skipAlphaCarbonTargets && Structures.isAlphaCarbonStructure(structure)) {
                        logger.info("ignored alpha carbon only structure {}", structure);
                        return null;
                    }
                    if (Fit3DAlignmentBatch.this.skipBackboneTargets && Structures.isBackboneStructure(structure)) {
                        logger.info("ignored backbone only structure {}", structure);
                        return null;
                    }
                    BranchSubstructure<?, ?> target = structure.getFirstModel();
                    logger.info("computing Fit3D alignment against {}", target);
                    // create Fit3DAlignment and decide between AtomFilter or RepresentationScheme
                    if (Fit3DAlignmentBatch.this.representationScheme == null) {
                        fit3d = Fit3DBuilder.create()
                                .query(Fit3DAlignmentBatch.this.queryMotif)
                                .target(target)
                                .atomFilter(Fit3DAlignmentBatch.this.atomFilter)
                                .rmsdCutoff(Fit3DAlignmentBatch.this.rmsdCutoff)
                                .distanceTolerance(Fit3DAlignmentBatch.this.distanceTolerance)
                                .statisticalModel(Fit3DAlignmentBatch.this.statisticalModel)
                                .run();
                    } else {
                        fit3d = Fit3DBuilder.create()
                                .query(Fit3DAlignmentBatch.this.queryMotif)
                                .target(target)
                                .representationScheme(Fit3DAlignmentBatch.this.representationScheme.getType())
                                .rmsdCutoff(Fit3DAlignmentBatch.this.rmsdCutoff)
                                .distanceTolerance(Fit3DAlignmentBatch.this.distanceTolerance)
                                .statisticalModel(Fit3DAlignmentBatch.this.statisticalModel)
                                .run();
                    }
                    return fit3d.getMatches();
                } catch (Fit3DException | StructureParserException | SubstructureSuperimpositionException | UncheckedIOException e) {
                    logger.warn("failed to run Fit3D against structure {}", Fit3DAlignmentBatch.this.multiParser.getCurrentPdbIdentifier(), e);
                }
            }
            return null;
        }
    }
}
