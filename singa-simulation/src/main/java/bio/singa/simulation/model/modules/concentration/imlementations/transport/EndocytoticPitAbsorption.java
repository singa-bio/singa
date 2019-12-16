package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.features.CargoAdditionRate;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.features.ScalingEntities;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.qualitative.implementations.EndocytoticPit;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class EndocytoticPitAbsorption extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    private static boolean isCollectingEndocytoticPit(Updatable updatable) {
        return updatable instanceof EndocytoticPit && ((EndocytoticPit) updatable).isCollecting();
    }

    public void postConstruct() {
        setApplicationCondition(EndocytoticPitAbsorption::isCollectingEndocytoticPit);
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        getRequiredFeatures().add(CargoAdditionRate.class);
        getRequiredFeatures().add(Cargoes.class);
        getRequiredFeatures().add(ScalingEntities.class);
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        EndocytoticPit currentPit = ((EndocytoticPit) supplier.getCurrentUpdatable());
        // get cargoes
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        // get base addition rate
        double additionRate = getScaledFeature(CargoAdditionRate.class);
        // get entities that scale cargo addition
        ScalingEntities scalingEntities = getFeature(ScalingEntities.class);
        ChemicalEntity catalyzingEntity = scalingEntities.getContent().get(0);
        ChemicalEntity inhibitingEntity = scalingEntities.getContent().get(1);
        // determine rate modifier
        double catalyzingConcentration = currentPit.getAssociatedNode().getConcentrationContainer().get(MEMBRANE, catalyzingEntity);
        double inhibitingConcentration = currentPit.getAssociatedNode().getConcentrationContainer().get(MEMBRANE, inhibitingEntity);
        // if everything is catalyzing use maximal rate, else scale linear with added inhibition and reduced catalysis
        double rateModifier = catalyzingConcentration / (catalyzingConcentration + inhibitingConcentration);
        double appliedRate = rateModifier * additionRate;
        // apply for all cargoes
        for (ChemicalEntity cargo : cargoes) {
            double concentration =  currentPit.getAssociatedNode().getConcentrationContainer().get(MEMBRANE, cargo);
            double concentrationDelta = appliedRate * concentration;
            // add to pit
            deltas.put(new ConcentrationDeltaIdentifier(currentPit, currentPit.getCellRegion().getMembraneSubsection(), cargo),
                    new ConcentrationDelta(this, currentPit.getCellRegion().getMembraneSubsection(), cargo, concentrationDelta));
            // remove from associated membrane
            deltas.put(new ConcentrationDeltaIdentifier(currentPit.getAssociatedNode(), currentPit.getAssociatedNode().getCellRegion().getMembraneSubsection(), cargo),
                    new ConcentrationDelta(this, currentPit.getAssociatedNode().getCellRegion().getMembraneSubsection(), cargo, -concentrationDelta));
        }
        return deltas;
    }


}
