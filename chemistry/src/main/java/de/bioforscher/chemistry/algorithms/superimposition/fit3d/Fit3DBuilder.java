package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A builder that guides through the creation of a {@link Fit3D} alignment.
 *
 * @author fk
 */
public class Fit3DBuilder {

    private static final double DEFAULT_DISTANCE_TOLERANCE = 1.0;
    private static final double DEFAULT_RMSD_CUTOFF = 2.5;
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();

    /**
     * prevent instantiation
     */
    private Fit3DBuilder() {

    }

    /**
     * Creates a new instance of the actual {@link Builder}.
     *
     * @return The {@link QueryStep} to define the query motif.
     */
    public static QueryStep create() {
        return new Builder();
    }

    public interface QueryStep {
        /**
         * Defines the query motif for this {@link Fit3D} search.
         *
         * @param query The query motif to be used.
         * @return The {@link TargetStep} to define one or several targets.
         */
        TargetStep query(StructuralMotif query);
    }

    public interface TargetStep {
        /**
         * Defines the target against which this {@link Fit3D} search should be run.
         *
         * @param target The target {@link BranchSubstructure} against which the search should be run.
         * @return The {@link AtomStep} to define optional restrictions on {@link Atom}s.
         */
        AtomStep target(BranchSubstructure<?> target);

        /**
         * Defines the targets against which this Fit3D search should be run in batch mode. This can either be a list
         * of PDB-IDs or file paths pointing to target files in PDB format.
         *
         * @param targets The targets against the search should be run in batch mode.
         * @return The {@link ParallelStep} to define the level of parallelism the batch search should use.
         */
        ParallelStep targets(List<String> targets);
    }

    public interface ParallelStep {
        /**
         * Defines for a batch search the level of parallelism (number of cores) that should be used by {@link Fit3D}.
         *
         * @param limitedParallelism The desired level of parallelism.
         * @return The {@link AtomStep} to define optional restrictions on {@link Atom}s.
         */
        AtomStep limitedParallelism(int limitedParallelism);

        /**
         * Allows Fit3D in search mode to use all available cores for processing.
         *
         * @return The {@link AtomStep} to define optional restrictions on {@link Atom}s.
         */
        AtomStep maximalParallelism();
    }

    public interface AtomStep {
        /**
         * Creates a new {@link Fit3D} search and starts calculation.
         *
         * @return A new {@link Fit3D} search when finished.
         */
        Fit3D run();

        /**
         * Defines a {@link AtomFilter} to be used for the {@link Fit3D} alignment (e.g. only sidechain atoms).
         *
         * @param atomFilter The {@link AtomFilter} to be used for the alignment.
         * @return The {@link ParameterStep} that can be used to define optional parameters.
         */
        ParameterStep atomFilter(Predicate<Atom> atomFilter);

        /**
         * Defines a single point {@link RepresentationScheme} that should be used to represent single residues.
         *
         * @param representationSchemeType The {@link RepresentationSchemeType} that should be used.
         * @return The {@link ParameterStep} that can be used to define optional parameters.
         */
        ParameterStep representationScheme(RepresentationSchemeType representationSchemeType);
    }

    public interface ParameterStep {
        /**
         * Creates a new {@link Fit3D} search and starts calculation.
         *
         * @return A new {@link Fit3D} search when finished.
         */
        Fit3D run();

        /**
         * Defines the RMSD cutoff up to which matches should be reported.
         *
         * @param rmsdCutoff The RMSD cutoff up to which alignments should be reported.
         * @return The {@link ParameterStep} that can be used to define optional parameters.
         */
        ParameterStep rmsdCutoff(double rmsdCutoff);

        /**
         * Defines the distance tolerance that is accepted when extracting local environments.
         *
         * @param distanceTolerance The distance tolerance considered when extracting local environments.
         * @return The {@link ParameterStep} that can be used to define optional parameters.
         */
        ParameterStep distanceTolerance(double distanceTolerance);
    }

    public static class Builder implements QueryStep, TargetStep, ParallelStep, AtomStep, ParameterStep {

        StructuralMotif queryMotif;
        BranchSubstructure<?> target;
        List<String> targetStructures;
        int parallelism;
        double rmsdCutoff = DEFAULT_RMSD_CUTOFF;
        double distanceTolerance = DEFAULT_DISTANCE_TOLERANCE;
        Predicate<Atom> atomFilter = DEFAULT_ATOM_FILTER;
        RepresentationScheme representationScheme;

        @Override
        public TargetStep query(StructuralMotif query) {
            Objects.requireNonNull(query);
            this.queryMotif = query;
            return this;
        }

        @Override
        public AtomStep target(BranchSubstructure<?> target) {
            Objects.requireNonNull(target);
            this.target = target;
            return this;
        }

        @Override
        public ParallelStep targets(List<String> targets) {
            Objects.requireNonNull(targets);
            if (targets.isEmpty()) {
                throw new Fit3DException("targetStructures cannot be empty");
            }
            this.targetStructures = targets;
            return this;
        }

        @Override
        public Fit3D run() {
            if (this.targetStructures != null) {
                return new Fit3DAlignmentBatch(this);
            }
            return new Fit3DAlignment(this);
        }

        @Override
        public ParameterStep rmsdCutoff(double rmsdCutoff) {
            if (rmsdCutoff <= 0.0) {
                throw new Fit3DException("RMSD cutoff must be positive");
            }
            this.rmsdCutoff = rmsdCutoff;
            return this;
        }

        @Override
        public ParameterStep distanceTolerance(double distanceTolerance) {
            if (distanceTolerance <= 0.0) {
                throw new Fit3DException("distance tolerance cutoff must be positive");
            }
            this.distanceTolerance = distanceTolerance;
            return this;
        }

        @Override
        public ParameterStep atomFilter(Predicate<Atom> atomFilter) {
            Objects.requireNonNull(atomFilter);
            this.atomFilter = atomFilter;
            return this;
        }

        @Override
        public ParameterStep representationScheme(RepresentationSchemeType representationSchemeType) {
            Objects.requireNonNull(representationSchemeType);
            this.representationScheme = RepresentationSchemeFactory.createRepresentationScheme(representationSchemeType);
            return this;
        }

        @Override
        public AtomStep limitedParallelism(int limitedParallelism) {
            if (limitedParallelism <= 0.0) {
                throw new Fit3DException("level of parallelism for batch calculation must be positive");
            }
            this.parallelism = limitedParallelism;
            return this;
        }

        @Override
        public AtomStep maximalParallelism() {
            this.parallelism = Runtime.getRuntime().availableProcessors();
            return this;
        }
    }
}
