package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.*;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransport extends AbstractNeighbourIndependentModule {

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();
    static {
        requiredFeatures.add(OsmoticPermeability.class);
    }

    public static TransporterStep inSimulation(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    private Transporter transporter;
    private ChemicalEntity cargo;
    private Set<ChemicalEntity> solutes;

    private SingleFileChannelMembraneTransport(Simulation simulation) {
        super(simulation);
        solutes = new HashSet<>();
        // apply this module only to membranes
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
    }

    private void initialize() {
        // reference entities for this module
        addReferencedEntity(transporter);
        addReferencedEntity(cargo);
        addReferencedEntities(solutes);
        // reference module in simulation
        addModuleToSimulation();
    }

    @Override
    public void checkFeatures() {
        if (!transporter.hasFeature(OsmoticPermeability.class)) {
            transporter.setFeature(OsmoticPermeability.class);
        }
    }

    /**
     * Only apply, if this is the outer phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param container
     * @return
     */
    private boolean onlyOuterPhase(ConcentrationContainer container) {
        return isOuterPhase(container) && isCargo() && transporterInMembrane(container);
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer container) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
            value = getSoluteDelta(container) * permeability * container.get(MEMBRANE, transporter).getValue().doubleValue() * 10000;
        } else {
            value = 0.0;
        }
        return new Delta(this, container.getOuterSubsection(), entity, Quantities.getQuantity(value, Environment.getTransformedMolarConcentration()));
    }

    /**
     * Only apply, if this is the inner phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param container
     * @return
     */
    private boolean onlyInnerPhase(ConcentrationContainer container) {
        return isInnerPhase(container) && isCargo() && transporterInMembrane(container);
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer container) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
            value = getSoluteDelta(container) * permeability * container.get(MEMBRANE, transporter).getValue().doubleValue() * 10000;
        } else {
            value = 0.0;
        }
        return new Delta(this, container.getInnerSubsection(), entity, Quantities.getQuantity(value, Environment.getTransformedMolarConcentration()));
    }

    private double getSoluteDelta(ConcentrationContainer container) {
        // sum outer solutes
        double outerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            outerConcentration += container.get(OUTER, solute).getValue().doubleValue();
        }
        // sum inner solutes
        double innerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            innerConcentration += container.get(INNER, solute).getValue().doubleValue();
        }
        // return delta
        return isInnerPhase(container) ?  innerConcentration - outerConcentration : outerConcentration - innerConcentration;
    }

    private boolean isCargo() {
        return getCurrentChemicalEntity().equals(cargo);
    }

    private boolean transporterInMembrane(ConcentrationContainer container) {
        return container.get(container.getMembraneSubsection(), transporter).getValue().doubleValue() != 0.0;
    }

    private boolean isOuterPhase(ConcentrationContainer container) {
        return getCurrentCellSection().equals(container.getOuterSubsection());
    }

    private boolean isInnerPhase(ConcentrationContainer container) {
        return getCurrentCellSection().equals(container.getInnerSubsection());
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + transporter.getName() + ")";
    }


    public interface TransporterStep {
        CargoStep transporter(Transporter transporter);
    }

    public interface CargoStep {
        SolutesStep cargo(ChemicalEntity cargo);
    }

    public interface SolutesStep {
        BuildStep forSolute(ChemicalEntity chemicalEntity);

        BuildStep forSolutes(ChemicalEntity ... chemicalEntities );

        BuildStep forSolutes(Collection<ChemicalEntity> chemicalEntities);
    }

    public interface BuildStep {
        SingleFileChannelMembraneTransport build();
    }

    public static class SingleFileChannelMembraneTransportBuilder implements TransporterStep, CargoStep, SolutesStep, BuildStep {

        SingleFileChannelMembraneTransport module;

        public SingleFileChannelMembraneTransportBuilder(Simulation simulation) {
            module = new SingleFileChannelMembraneTransport(simulation);
        }

        @Override
        public CargoStep transporter(Transporter transporter) {
            module.transporter = transporter;
            return this;
        }

        @Override
        public SolutesStep cargo(ChemicalEntity cargo) {
            module.cargo = cargo;
            return this;
        }

        @Override
        public BuildStep forSolute(ChemicalEntity solute) {
            module.solutes.add(solute);
            return this;
        }

        @Override
        public BuildStep forSolutes(ChemicalEntity... solutes) {
            module.solutes.addAll(Arrays.asList(solutes));
            return this;
        }

        @Override
        public BuildStep forSolutes(Collection<ChemicalEntity> solutes) {
            module.solutes.addAll(solutes);
            return this;
        }

        @Override
        public SingleFileChannelMembraneTransport build() {
            module.initialize();
            return module;
        }
    }

}
