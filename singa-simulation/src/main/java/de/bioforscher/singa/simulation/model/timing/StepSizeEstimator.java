package de.bioforscher.singa.simulation.model.timing;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.membranetransport.PassiveMembraneTransport;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;
import java.util.Set;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class StepSizeEstimator {

    public static void main(String[] args) {

        EnvironmentalParameters.getInstance().setNodeDistance(Quantities.getQuantity(100, NANO(METRE)));

        ChemicalEntity<?> entity = new Species.Builder("A")
                .assignFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED))
                .build();

        MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
        membraneEntry.scale(EnvironmentalParameters.getInstance().getTimeStep());
        Quantity<Frequency> membraneEntryScaled = membraneEntry.getScaledQuantity();

        EnclosedCompartment left = new EnclosedCompartment("LC", "Left");
        EnclosedCompartment right = new EnclosedCompartment("RC", "Right");
        Membrane membrane = Membrane.forCompartment(left);

        BioNode node = new BioNode(0);
        node.setCellSection(membrane);
        node.setConcentrations(new MembraneContainer(right, left, membrane));

        // concentration form paper "A liposomal fluorescence assay to study permeation... Eyer et al."
        node.setAvailableConcentration(entity, right, Quantities.getQuantity(20, MICRO(MOLE_PER_LITRE)).to(MOLE_PER_LITRE));

        PassiveMembraneTransport transport = new PassiveMembraneTransport();

        // for each phenomenon there is a optimal time step
        // the the time step is unknown in advance
        // we estimate a good time by comparing the difference in two changes using similar time steps

        // a epsilon is defined that describes the upper tolerance for change in any time step epsilon "ep"
        double epsilon = 0.1;

        // additionally the an time step is guessed
        // each phenomenon defines an estimated save time step "hi" for any node distance
        Quantity<Time> fullStep = Quantities.getQuantity(10.0, NANO(SECOND));
        // use half of this timestep (hi/2) to estimate the difference or error in changes
        // Quantity<Time> halfSaveStep = fullStep.divide(2.0);

        // therefore calculate the change for "hi" for any node
        // and determine the largest possible change and where (which node) "nhi" it would occur

        boolean timestepApplicable = false;
        while (!timestepApplicable) {

            // determine deltas at hi
            EnvironmentalParameters.getInstance().setTimeStep(fullStep);
            Set<ConcentrationDelta> fullDeltas = transport.calculateDelta(node, entity);
            // System.out.println(fullDeltas);

            // determine deltas at hi/2
            // EnvironmentalParameters.getInstance().setTimeStep(halfSaveStep);
            // Set<ConcentrationDelta> halfDeltas = transport.calculateDelta(node, entity);
            // System.out.println(halfDeltas);

            // find most negative change "dchi"
            double fullDelta = 0.0;
            CellSection cellSection = null;
            for (ConcentrationDelta delta : fullDeltas) {
                double currentDelta = delta.getQuantity().getValue().doubleValue();
                if (currentDelta < fullDelta) {
                    // remember non abs value
                    fullDelta = currentDelta;
                    cellSection = delta.getCellSection();
                }
            }

            // get corresponding change "dchi2" in the half step calculation
            //        Objects.requireNonNull(cellSection);
            //        CellSection finalCellSection = cellSection;
            //        ConcentrationDelta correspondingDelta = halfDeltas.stream()
            //                .filter(halfDelta -> halfDelta.getCellSection().equals(finalCellSection))
            //                .findFirst()
            //                .orElse(null);
            //
            //        Objects.requireNonNull(correspondingDelta);
            //        double halfDelta = correspondingDelta.getQuantity().getValue().doubleValue();

            // check if subtraction would result in overflow
            double currentConcentration = node.getAvailableConcentration(entity, cellSection).getValue().doubleValue();

            double percentChange = Math.abs(fullDelta / currentConcentration);

            if (percentChange <= epsilon && -fullDelta < currentConcentration) {
                System.out.println("Current time step is applicable " + fullStep + ", resulting in " + percentChange + "-fold change.");
                timestepApplicable = true;
            } else {
                System.out.println("Current time step " + fullStep + " is too large, resulting in " + percentChange + "-fold change.");
                fullStep = fullStep.divide(2.0);
            }
        }


        // calculate the difference between the change in hi "dchi" and hi/2 "dchi2"
        // the local error shall be E = abs(dchi - dchi2)
        // double localError = Math.abs(fullDelta - halfDelta);
        // System.out.println("local error: " + localError);

        // NOT SURE FROM HERE
        // and the local error is proportional to the square of the step size (Taylor's theorem)
        // therefore E = c*hiˆ2
        // and since we have given a threshold ("ep") our best step size would be ep = c*hb
        // we want hb = hi * sqrt(E) and E = ep / (abs(dchi - dchi2))
        // TO HERE


        // Diffusion needs to occur globally since it is neighbour dependant
        // if diffusion occurs everything needs to sync up to this time step


        // for multiple phenomena we have to take care
        // a "slow" process requires few updates
        // a "fast" process needs to be updated frequently
        // slow processes need to monitor changes and figure out their time to intervene


    }


}
