package de.bioforscher.singa.structure.algorithms.superimposition.consensus;


import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;

import java.util.List;
import java.util.function.Predicate;

import static de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * A builder that guides through the creation of a {@link ConsensusAlignment}.
 *
 * @author fk
 */
public class ConsensusBuilder {

    /**
     * Default values for the {@link ConsensusAlignment} algorithm.
     */
    private static final double DEFAULT_CLUSTER_CUTOFF = 0.5;
    private static final boolean DEFAULT_ALIGN_WITHIN_CLUSTERS = true;
    private static final Predicate<Atom> DEFAULT_ATOM_FILTER = AtomFilter.isArbitrary();
    private static final RepresentationSchemeType DEFAULT_REPRESENTATION_SCHEME_TYPE = null;
    private static final boolean DEFAULT_IDEAL_SUPERIMPOSITION = false;

    /**
     * prevent instantiation
     */
    private ConsensusBuilder() {

    }

    /**
     * Creates a new instance of the actual {@link Builder}.
     *
     * @return The {@link InputStep} to define the input structures.
     */
    public static InputStep create() {
        return new Builder();
    }

    public interface InputStep {
        /**
         * Defines the input {@link StructuralMotif}s for the {@link ConsensusAlignment}.
         *
         * @param structuralMotifs The input {@link StructuralMotif}s to be used.
         * @return The {@link AtomStep} to define {@link RepresentationSchemeType} or {@link AtomFilter}.
         */
        AtomStep inputStructuralMotifs(List<StructuralMotif> structuralMotifs);
    }

    public interface AtomStep {

        /**
         * Defines a {@link RepresentationSchemeType} to be used during consensus calculation. This is exclusive to the
         * definition of an {@link AtomFilter}.
         *
         * @param representationSchemeType The {@link RepresentationSchemeType} to be used.
         * @return The {@link ParameterStep} to define additional parameters.
         */
        ParameterStep representationSchemeType(RepresentationSchemeType representationSchemeType);

        /**
         * Defines the {@link AtomFilter} to be used during consensus calculation. This is exclusive to the definition
         * of a {@link RepresentationSchemeType}.
         *
         * @param atomFilter The {@link AtomFilter} to be used.
         * @return The {@link ParameterStep} to define additional parameters.
         */
        ParameterStep atomFilter(Predicate<Atom> atomFilter);

        /**
         * Creates a new {@link ConsensusAlignment} and starts the calculation.
         *
         * @return A new {@link ConsensusAlignment} once calculation has finished.
         */
        ConsensusAlignment run();
    }

    public interface ParameterStep {

        ConsensusAlignment run();

        ParameterStep clusterCutoff(double clusterCutoff);

        ParameterStep idealSuperimposition(boolean idealSuperimposition);

        ParameterStep alignWithinClusters(boolean alignWithinClusters);
    }

    public static class Builder implements InputStep, AtomStep, ParameterStep {

        List<StructuralMotif> structuralMotifs;
        RepresentationSchemeType representationSchemeType = DEFAULT_REPRESENTATION_SCHEME_TYPE;
        Predicate<Atom> atomFilter = DEFAULT_ATOM_FILTER;
        double clusterCutoff = DEFAULT_CLUSTER_CUTOFF;
        boolean idealSuperimposition = DEFAULT_IDEAL_SUPERIMPOSITION;
        boolean alignWithinClusters = DEFAULT_ALIGN_WITHIN_CLUSTERS;

        @Override
        public AtomStep inputStructuralMotifs(List<StructuralMotif> structuralMotifs) {
            this.structuralMotifs = structuralMotifs;
            return this;
        }

        @Override
        public ParameterStep representationSchemeType(RepresentationSchemeType representationSchemeType) {
            this.representationSchemeType = representationSchemeType;
            return this;
        }

        @Override
        public ParameterStep atomFilter(Predicate<Atom> atomFilter) {
            this.atomFilter = atomFilter;
            return this;
        }

        @Override
        public ConsensusAlignment run() {
            return new ConsensusAlignment(this);
        }

        @Override
        public ParameterStep clusterCutoff(double clusterCutoff) {
            this.clusterCutoff = clusterCutoff;
            return this;
        }

        @Override
        public ParameterStep idealSuperimposition(boolean idealSuperimposition) {
            this.idealSuperimposition = idealSuperimposition;
            return this;
        }

        @Override
        public ParameterStep alignWithinClusters(boolean alignWithinClusters) {
            this.alignWithinClusters = alignWithinClusters;
            return this;
        }
    }
}
