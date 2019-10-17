package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantConcentration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public abstract class AbstractKineticLaw implements KineticLaw {

    protected Reaction reaction;

    protected Map<Class<? extends Feature>, RateConstant> cachedFeature;

    public AbstractKineticLaw(Reaction reaction) {
        this.reaction = reaction;
        cachedFeature = new HashMap<>();
    }

    protected double getScaledRate(Class<? extends Feature> featureClass) {
        RateConstant rate = cachedFeature.get(featureClass);
        if (rate == null) {
            for (Feature<?> potentialRate : reaction.getFeatures()) {
                if (featureClass.isInstance(potentialRate)) {
                    rate = (RateConstant) potentialRate;
                    cachedFeature.put(featureClass, rate);
                    break;
                }
            }
        }
        if (rate == null) {
            throw new FeatureUnassignableException("Unable to access " + featureClass.getSimpleName() + " for reaction " + reaction.toString() + ".");
        }
        if (reaction.getSupplier().isStrutCalculation()) {
            return rate.getHalfScaledQuantity();
        }
        return rate.getScaledQuantity();
    }

    public  <T extends Feature>T getRate(Class<T> featureClass) {
        for (Feature<?> potentialRate : reaction.getFeatures()) {
            if (featureClass.isInstance(potentialRate)) {
                return (T) potentialRate;
            }
        }
        throw new FeatureUnassignableException("Unable to access " + featureClass.getSimpleName() + " for reaction " + reaction.toString() + ".");
    }


    protected double multiply(List<ReactantConcentration> reactants) {
        double concentration = 1.0;
        for (ReactantConcentration current : reactants) {
            concentration *= current.getConcentration();
        }
        return concentration;
    }

}
