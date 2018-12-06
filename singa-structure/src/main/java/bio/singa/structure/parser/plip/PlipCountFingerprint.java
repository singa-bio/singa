package bio.singa.structure.parser.plip;

import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class PlipCountFingerprint {

    private Map<InteractionType, Integer> counts = new TreeMap<>();
    private Vector vector;

    private PlipCountFingerprint() {
        Stream.of(InteractionType.values())
                .forEach(interactionType -> counts.putIfAbsent(interactionType, 0));
    }

    public static PlipCountFingerprint of(InteractionContainer interactionContainer) {
        PlipCountFingerprint plipCountFingerprint = new PlipCountFingerprint();
        for (Interaction interaction : interactionContainer.getInteractions()) {
            plipCountFingerprint.counts.merge(interaction.getInteractionType(), 1, Integer::sum);
        }
        return plipCountFingerprint;
    }

    public Vector getVector() {
        if (vector == null) {
            double[] elements = counts.values().stream()
                    .mapToDouble(Double::valueOf)
                    .toArray();
            vector = new RegularVector(elements);
        }
        return vector;
    }
}
