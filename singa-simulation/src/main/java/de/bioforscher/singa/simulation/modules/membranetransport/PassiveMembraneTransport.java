package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module {

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransport.class);

    private Set<ChemicalEntity<?>> chemicalEntities;

    public PassiveMembraneTransport() {
        this.chemicalEntities = new HashSet<>();
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        for (BioNode node : graph.getNodes()) {
            if (node.getState() == NodeState.MEMBRANE) {
                for (ChemicalEntity<?> entity : node.getAllReferencedEntities()) {
                    calculateDelta(node, entity);
                }
            }
        }
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.chemicalEntities;
    }

    public Set<ConcentrationDelta> calculateDelta(BioNode node, ChemicalEntity<?> entity) {
        // scale potentially not already scaled parameters
        setUpParameters(entity);

        MembraneContainer concentrations = (MembraneContainer) node.getConcentrations();
        double outerPhase = concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue();
        double outerLayer = concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
        double innerLayer = concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
        double innerPhase = concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue();

        // membrane entry (outer phase -> outer layer and inner phase -> inner layer) - kIn
        double kIn = entity.getFeature(MembraneEntry.class).getScaledQuantity().getValue().doubleValue();
        // membrane exit (outer layer -> outer phase and inner layer -> inner phase) - kOut
        double kOut = entity.getFeature(MembraneExit.class).getScaledQuantity().getValue().doubleValue();
        // flip-flip across membrane (outer layer <-> inner layer) - kFlip
        double kFlip = entity.getFeature(MembraneFlipFlop.class).getScaledQuantity().getValue().doubleValue();

        // for highly polar molecules kIn and kFlip determine overall permeation speed which is slow
        // as lipophilicity increases kIn becomes high and and kFlip and kOut are roughly equivalent
        // leading to a optimal permeation
        // at high lipophilicity kOut becomes low and the compound tends to reside in the membrane
        // four concentrations (ao, lo, li, ai) have to be set
        // (outer phase) ao = -kIn * ao + kOut * lo
        // (outer layer) lo = kIn * ao - (kOut + kFlip) * lo + kFlip * li
        // (inner layer) li = kIn * ai - (kOut + kFlip) * li + kFlip * lo
        // (inner phase) ai = -kIn * ai + kOut * li

        double newOuterPhase = -kIn * outerPhase + kOut * outerLayer;
        double newOuterLayer = kIn * outerPhase - (kOut + kFlip) * outerLayer + kFlip * innerLayer;
        double newInnerLayer = kIn * innerPhase - (kOut + kFlip) * innerLayer + kFlip * outerLayer;
        double newInnerPhase = -kIn * innerPhase + kOut * innerLayer;

        // setup and return updates
        Set<ConcentrationDelta> deltas = new HashSet<>();
        ConcentrationDelta dOP = new ConcentrationDelta(concentrations.getOuterPhaseSection(), entity, Quantities.getQuantity(newOuterPhase, MOLE_PER_LITRE));
        ConcentrationDelta dOL = new ConcentrationDelta(concentrations.getOuterLayerSection(), entity, Quantities.getQuantity(newOuterLayer, MOLE_PER_LITRE));
        ConcentrationDelta dIL = new ConcentrationDelta(concentrations.getInnerLayerSection(), entity, Quantities.getQuantity(newInnerLayer, MOLE_PER_LITRE));
        ConcentrationDelta dIP = new ConcentrationDelta(concentrations.getInnerPhaseSection(), entity, Quantities.getQuantity(newInnerPhase, MOLE_PER_LITRE));
        // add deltas tp node
        node.addDelta(dOP);
        node.addDelta(dOL);
        node.addDelta(dIL);
        node.addDelta(dIP);
        // return deltas
        deltas.add(dOP);
        deltas.add(dOL);
        deltas.add(dIL);
        deltas.add(dIP);
        return deltas;
    }

    private void setUpParameters(ChemicalEntity<?> entity) {
         if (!this.chemicalEntities.contains(entity)) {
            // entry
            MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
            membraneEntry.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // exit
            MembraneExit membraneExit = entity.getFeature(MembraneExit.class);
            membraneExit.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // flip flop
            MembraneFlipFlop membraneFlipFlop = entity.getFeature(MembraneFlipFlop.class);
            membraneFlipFlop.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // add to set
            this.chemicalEntities.add(entity);
         }
    }

}
