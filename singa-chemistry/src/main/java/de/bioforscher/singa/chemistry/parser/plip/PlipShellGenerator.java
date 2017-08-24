package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.families.LigandFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlipShellGenerator {

    private final Chain chain;
    private final LeafSubstructure<?, ?> reference;
    private final InteractionContainer interChainInteractions;
    private final InteractionContainer referenceInteractions;
    private Map<InteractionShell, List<LeafSubstructure<?, ?>>> shells;

    private PlipShellGenerator(Chain chain, LeafSubstructure<?, ?> reference, InteractionContainer interChainInteractions,
                               InteractionContainer referenceInteractions) {

        if (!(reference instanceof AtomContainer && reference.getFamily() instanceof LigandFamily)) {
            throw new IllegalArgumentException("reference must be a valid ligand");
        }

        this.chain = chain;
        this.reference = reference;
        this.interChainInteractions = interChainInteractions;
        this.referenceInteractions = referenceInteractions;
        computeShells();
    }

    public static Map<InteractionShell, List<LeafSubstructure<?, ?>>> getInteractionShellsForLigand(Chain chain, LeafSubstructure<?, ?> reference, InteractionContainer interChainInteractions, InteractionContainer referenceInteractions) {
        PlipShellGenerator plipShellGenerator = new PlipShellGenerator(chain, reference, interChainInteractions, referenceInteractions);
        return plipShellGenerator.getShells();
    }

    public Map<InteractionShell, List<LeafSubstructure<?, ?>>> getShells() {
        return shells;
    }

    private void computeShells() {
        // compute first shell (directly interacting with reference)
        List<LeafSubstructure<?, ?>> firstShell = referenceInteractions.getInteractions().stream()
                .map(interaction -> interaction.source)
                .map(leafIdentifier -> chain.getLeafSubstructures().stream()
                        .filter(leafSubstructure -> leafSubstructure.getIdentifier().equals(leafIdentifier))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        shells.put(InteractionShell.FIRST, firstShell);

        for (int i = 1; i < InteractionShell.values().length; i++) {
            getNextShell(InteractionShell.values()[i - 1]);
        }
    }

    private void getNextShell(InteractionShell previousShell) {
        List<LeafSubstructure<?, ?>> previousLeafSubstructures = shells.get(previousShell);
        for (LeafSubstructure<?, ?> previousLeafSubstructure : previousLeafSubstructures) {
//            interChainInteractions.getInteractions().stream()
//                    .filter(interaction -> previousLeafSubstructure.getIdentifier().equals(interaction.getSource()) ||
//                            previousLeafSubstructure.getIdentifier().equals(interaction.getTarget()))
//                    .collect(Collectors.toList());
        }
    }

    public enum InteractionShell {
        FIRST, SECOND, THIRD
    }
}
