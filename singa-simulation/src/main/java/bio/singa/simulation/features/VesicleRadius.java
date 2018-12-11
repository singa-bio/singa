package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class VesicleRadius extends AbstractFeature<Quantity<Length>> {

    public static final VesicleRadius DEFAULT_VESICLE_RADIUS = new VesicleRadius(Quantities.getQuantity(50.0, NANO(METRE)), DefaultFeatureSources.EHRLICH2004);

    private static final String SYMBOL = "r_Vesicle";

    public VesicleRadius(Quantity<Length> radius, Evidence evidence) {
        super(radius, evidence);
    }

    public VesicleRadius(double radius, Evidence evidence) {
        super(Quantities.getQuantity(radius, NANO(METRE)), evidence);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
