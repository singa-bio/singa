package bio.singa.features.formatter;

import javax.measure.Quantity;

/**
 * @author cl
 */
public interface QuantityFormatter<UnitType extends Quantity<UnitType>>  {

    String format(Quantity<UnitType> quantity);

    String format(double defaultQuantity);

}
