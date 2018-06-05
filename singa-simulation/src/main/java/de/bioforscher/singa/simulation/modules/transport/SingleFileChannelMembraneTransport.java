package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNodeSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.*;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.*;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransport extends AbstractNodeSpecificModule {

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
        addDeltaFunction(this::calculateDeltas, this::hasMembrane);
    }

    private boolean hasMembrane(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(MEMBRANE) != null;
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

    private List<Delta> calculateDeltas(ConcentrationContainer container) {
        List<Delta> deltas = new ArrayList<>();
        final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
        final double value = getSoluteDelta(container) * permeability * MolarConcentration.concentrationToMolecules(container.get(MEMBRANE, transporter), Environment.getSubsectionVolume()).getValue().doubleValue();
        deltas.add(new Delta(this, container.getOuterSubsection(), cargo, Quantities.getQuantity(value, Environment.getConcentrationUnit())));
        deltas.add(new Delta(this, container.getInnerSubsection(), cargo, Quantities.getQuantity(-value, Environment.getConcentrationUnit())));
        return deltas;
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
        return innerConcentration - outerConcentration;
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

        BuildStep forSolutes(ChemicalEntity... chemicalEntities);

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
