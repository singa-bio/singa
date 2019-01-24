package bio.singa.features.formatter;

import bio.singa.features.quantities.MolarConcentration;

/**
 * @author cl
 */
public interface GeneralConcentrationFormatter extends QuantityFormatter<MolarConcentration> {

    String format(double concentrationValue);

}
