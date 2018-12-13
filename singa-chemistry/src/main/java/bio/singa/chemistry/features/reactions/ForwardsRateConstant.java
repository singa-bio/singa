package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Feature;

import javax.measure.Quantity;

/**
 * @author cl
 */
public interface ForwardsRateConstant<ReactionRateType extends ReactionRate<ReactionRateType>> extends Feature<Quantity<ReactionRateType>> {

}
