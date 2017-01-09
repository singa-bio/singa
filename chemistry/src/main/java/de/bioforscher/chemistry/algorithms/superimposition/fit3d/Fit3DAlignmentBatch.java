package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A parallel version of the {@link Fit3DAlignment} for substructure search in a set of target structures.
 *
 * @author fk
 */
public class Fit3DAlignmentBatch {

    private static final Logger logger = LoggerFactory.getLogger(Fit3DAlignmentBatch.class);

    private StructuralMotif queryMotif;
    private List<String> targetStructures;
    private ExecutorService executorService;
    private TreeMap<Double, SubstructureSuperimposition> allMatches;

    public Fit3DAlignmentBatch(StructuralMotif queryMotif, List<String> targetStructures) {
        this.queryMotif = queryMotif;
        this.targetStructures = targetStructures;
        this.executorService = Executors.newWorkStealingPool();
        logger.info("Fit3D alignment batch initialized with {} target structures", targetStructures.size());
        computeAlignments();
        logger.info("found {} matches in {} target structures", this.allMatches.size(), targetStructures.size());
    }

    /**
     * creates jobs and executes them in parallel
     */
    private void computeAlignments() {
        List<Fit3DCalculator> jobs = this.targetStructures.stream()
                .map(Fit3DCalculator::new)
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
                    .collect(TreeMap::new, Map::putAll, Map::putAll);
        } catch (InterruptedException e) {
            logger.error("Ft3D parallel execution failed", e);
        }
    }

    /**
     * Returns all matches of this Fit3D batch calculation.
     *
     * @return Matches in all target structures.
     */
    public TreeMap<Double, SubstructureSuperimposition> getAllMatches() {
        return this.allMatches;
    }

    /**
     * Internal class for parallel calculation of {@link Fit3DAlignment}s.
     */
    private class Fit3DCalculator implements Callable<TreeMap<Double, SubstructureSuperimposition>> {

        private String targetStructure;

        private Fit3DCalculator(String targetStructure) {
            this.targetStructure = targetStructure;
        }

        @Override
        public TreeMap<Double, SubstructureSuperimposition> call() throws Exception {
            Fit3DAlignment fit3d = new Fit3DAlignment(Fit3DAlignmentBatch.this.queryMotif,
                    new File(this.targetStructure).exists() ?
                            PDBParserService.parsePDBFile(this.targetStructure).getAllChains().get(0) :
                            PDBParserService.parseProteinById(this.targetStructure).getAllChains().get(0));
            return fit3d.getMatches();
        }
    }
}
