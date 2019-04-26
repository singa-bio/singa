package bio.singa.features.quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;

/**
 * @author cl
 */
public class Geometry {

    /**
     * The volume of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The volume.
     */
    public static Quantity<Volume> calculateVolume(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(radius).multiply(Math.PI).multiply(4.0 / 3.0).asType(Volume.class);
    }

    /**
     * The area of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The area.
     */
    public static Quantity<Area> calculateArea(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(Math.PI).multiply(4.0).asType(Area.class);
    }

}
