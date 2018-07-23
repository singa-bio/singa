package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.fit3d.statistics.StatisticalModel;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.model.oak.Structures;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final boolean mapUniprotIdentifiers;
    private final boolean mapPfamIdentifiers;
    private final boolean mapEcNumbers;
    private final boolean filterEnvironments;
    private final double filterThreshold;
    private List<Fit3DMatch> allMatches;

    Fit3DAlignmentBatch(Fit3DBuilder.Builder builder) {
        queryMotif = builder.queryMotif;
        multiParser = builder.multiParser;
        parallelism = builder.parallelism;
        skipAlphaCarbonTargets = builder.skipAlphaCarbonTargets;
        skipBackboneTargets = builder.skipBackboneTargets;
        executorService = Executors.newWorkStealingPool(parallelism);
        atomFilter = builder.atomFilter;
        representationScheme = builder.representationScheme;
        rmsdCutoff = builder.rmsdCutoff;
        distanceTolerance = builder.distanceTolerance;
        statisticalModel = builder.statisticalModel;
        mapUniprotIdentifiers = builder.mapUniprotIdentifiers;
        mapPfamIdentifiers = builder.mapPfamIdentifiers;
        mapEcNumbers = builder.mapEcNumbers;
        filterEnvironments = builder.filterEnvironments;
        filterThreshold = builder.filterThreshold;
        logger.info("Fit3D alignment batch initialized with {} target structures", multiParser.getNumberOfQueuedStructures());
        computeAlignments();
        logger.info("found {} matches in {} target structures", allMatches.size(), multiParser.getNumberOfQueuedStructures());
    }

    /**
     * Creates jobs and executes them in parallel.
     */
    private void computeAlignments() {

        // create the exact number of jobs
        List<Fit3DCalculator> jobs = new ArrayList<>();
        for (int i = 0; i < multiParser.getNumberOfQueuedStructures(); i++) {
            jobs.add(new Fit3DCalculator());
        }

        try {
            allMatches = executorService.invokeAll(jobs).stream()
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

        Collections.sort(allMatches);

        // calculate statistics
        if (statisticalModel != null) {
            try {
                statisticalModel.calculatePvalues(allMatches);
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
        return allMatches;
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
        public List<Fit3DMatch> call() {
            // FIXME here we are dealing only with the first model
            Fit3D fit3d;
            if (multiParser.hasNext()) {
                Structure structure = null;
                try {
                    structure = multiParser.next();
                    if (skipAlphaCarbonTargets && Structures.isAlphaCarbonStructure(structure)) {
                        logger.info("ignored alpha carbon only structure {}", structure);
                        return null;
                    }
                    if (skipBackboneTargets && Structures.isBackboneStructure(structure)) {
                        logger.info("ignored backbone only structure {}", structure);
                        return null;
                    }
                    Model target = structure.getFirstModel();
                    logger.info("computing Fit3D alignment against {}", target);
                    // create Fit3DAlignment and decide between AtomFilter or RepresentationScheme
                    Fit3DBuilder.ParameterStep parameterStep;
                    if (representationScheme == null) {
                        parameterStep = Fit3DBuilder.create()
                                .query(queryMotif)
                                .target(target)
                                .atomFilter(atomFilter)
                                .rmsdCutoff(rmsdCutoff)
                                .distanceTolerance(distanceTolerance);
                    } else {
                        parameterStep = Fit3DBuilder.create()
                                .query(queryMotif)
                                .target(target)
                                .representationScheme(representationScheme.getType())
                                .rmsdCutoff(rmsdCutoff)
                                .distanceTolerance(distanceTolerance);
                    }

                    if (statisticalModel != null) {
                        parameterStep.statisticalModel(statisticalModel);
                    }

                    if (mapUniprotIdentifiers) {
                        parameterStep.mapUniProtIdentifiers();
                    }
                    if (mapPfamIdentifiers) {
                        parameterStep.mapPfamIdentifiers();
                    }
                    if (mapEcNumbers) {
                        parameterStep.mapECNumbers();
                    }
                    if (filterEnvironments) {
                        parameterStep.filterEnvironments(filterThreshold);
                    }
                    fit3d = parameterStep.run();

                    List<Fit3DMatch> matches = fit3d.getMatches();
                    for (Fit3DMatch match : matches) {
                        match.setStructureTitle(structure.getTitle());
                    }

                    return matches;
//                } catch (Fit3DException | StructureParserException | SubstructureSuperimpositionException | UncheckedIOException e) {
                } catch (Exception e) {
                    if (structure != null) {
                        logger.warn("failed to run Fit3D against structure {}", structure, e);
                    } else {
                        logger.warn("failed to run Fit3D", e);
                    }
                }
            }
            return null;
        }
    }
}
