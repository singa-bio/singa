package de.bioforscher.singa.structure.algorithms;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.AtomContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class StructureToSphere {

    public static List<Sphere> convert(AtomContainer leafSubstructureContainer) {
        List<Sphere> spheres = new ArrayList<>();
        for (Atom atom : leafSubstructureContainer.getAllAtoms()) {
            spheres.add(new Sphere(atom.getPosition(), atom.getElement().getVanDerWaalsRadius().getValue().doubleValue()));
        }
        return spheres;
    }

}
