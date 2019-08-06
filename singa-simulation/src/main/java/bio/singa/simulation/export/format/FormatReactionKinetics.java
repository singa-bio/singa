package bio.singa.simulation.export.format;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class FormatReactionKinetics {

    private static final String michaelisMentenTemplate = "$\\frac{k_{\\text{cat}} \\cdot [\\text{%s}] \\cdot [\\text{%s}]}{K_m \\cdot [\\text{%s}]}$";

    private static String formatSectionReactants(Collection<Reactant> reactants) {
        return reactants.stream().map(FormatReactionKinetics::formatSectionReactant)
                .collect(Collectors.joining(" \\cdot "));
    }

    private static String formatSectionReactant(Reactant reactant) {
        String section = null;
        switch (reactant.getPreferredTopology()) {
            case INNER:
                section = "i";
                break;
            case OUTER:
                section = "o";
                break;
            case MEMBRANE:
                section = "m";
                break;
        }
        return "[\\text{" + reactant.getEntity().getIdentifier() + "}]_" + section;
    }

    public static String formatTex(Reaction reaction) {
        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
        String substrates = formatSectionReactants(reaction.getReactantBehavior().getSubstrates());
        String products = formatSectionReactants(reaction.getReactantBehavior().getProducts());
        return "$k_{1} \\cdot " + substrates + " - k_{-1} \\cdot " + products + "$";
        } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
            String substrates = formatSectionReactants(reaction.getReactantBehavior().getSubstrates());
            return "$k \\cdot " + substrates + "$";
        } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
            String substrate = reaction.getReactantBehavior().getSubstrates().iterator().next().getEntity().getIdentifier();
            String enzyme = reaction.getReactantBehavior().getCatalysts().iterator().next().getEntity().getIdentifier();
            return String.format(michaelisMentenTemplate, enzyme, substrate, substrate);
        } else {
            return "not supported";
        }
    }

}
