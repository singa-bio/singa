package bio.singa.features.formatter;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

/**
 * @author cl
 */
public class MoleculeFormatter implements GeneralConcentrationFormatter {

    private static MoleculeFormatter instance = getInstance();

    public static MoleculeFormatter getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    private static void reinitialize() {
        synchronized (MoleculeFormatter.class) {
            instance = new MoleculeFormatter();
        }
    }

    @Override
    public String format(Quantity<MolarConcentration> quantity) {
        Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(quantity.to(UnitRegistry.getConcentrationUnit()).getValue().doubleValue());
        return molecules.getValue().toString();
    }

    public String format(double defaultQuantity) {
        return format(UnitRegistry.concentration(defaultQuantity));
    }

    public static String toMolecules(Quantity<MolarConcentration> quantity) {
        return getInstance().format(quantity);
    }

}
