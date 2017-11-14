package de.bioforscher.singa.chemistry.descriptive.features.transporterflux;

import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

/**
 * Represents the rate at which a substrate of a {@link Transporter} may be transported through the membrane.
 *
 * @author cl
 */
public class TransporterFlux extends RateConstant {

    public TransporterFlux(Quantity<Frequency> frequencyQuantity, FeatureOrigin featureOrigin) {
        super(frequencyQuantity, featureOrigin);
    }

    public TransporterFlux(double frequency, FeatureOrigin featureOrigin) {
        super(frequency, featureOrigin);
    }

}
