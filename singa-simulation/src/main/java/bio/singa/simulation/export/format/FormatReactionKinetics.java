package bio.singa.simulation.export.format;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.RuleBasedReactantBehavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class FormatReactionKinetics {

    private static final String michaelisMentenTemplate = "$\\frac{k_{\\text{cat}} \\cdot [\\text{%s}] \\cdot [\\text{%s}]}{K_m \\cdot [\\text{%s}]}$";

    public static List<String> formatTex(Reaction reaction) {
        List<String> resultingStrings = new ArrayList<>();
        if (reaction.getReactantBehavior() instanceof RuleBasedReactantBehavior) {
            List<ReactantSet> reactantSets = reaction.getReactantBehavior().getReactantSets();
            for (ReactantSet reactantSet : reactantSets) {
                String kineticsString;
                if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
                    kineticsString = formatReversibleKinetics(reactantSet.getSubstrates(),reactantSet.getProducts());
                } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
                    kineticsString = formatIrreversibleKinetics(reactantSet.getSubstrates());
                } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
                    String substrate = reactantSet.getSubstrates().iterator().next().getEntity().getIdentifier();
                    String enzyme = reactantSet.getCatalysts().iterator().next().getEntity().getIdentifier();
                    kineticsString = formatMichaelisMentenKinetics(substrate, enzyme);
                } else {
                    kineticsString = "";
                }
                resultingStrings.add(kineticsString);
            }
        } else {
            String kineticsString;
            if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
                kineticsString = formatReversibleKinetics(reaction.getReactantBehavior().getSubstrates(),reaction.getReactantBehavior().getProducts());
            } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
                kineticsString = formatIrreversibleKinetics(reaction.getReactantBehavior().getSubstrates());
            } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
                String substrate = reaction.getReactantBehavior().getSubstrates().iterator().next().getEntity().getIdentifier();
                String enzyme = reaction.getReactantBehavior().getCatalysts().iterator().next().getEntity().getIdentifier();
                kineticsString = formatMichaelisMentenKinetics(substrate, enzyme);
            } else {
                kineticsString = "";
            }
            resultingStrings.add(kineticsString);
        }
        return resultingStrings;
    }

    private static String formatReversibleKinetics(List<Reactant> substrates, List<Reactant> products) {
        String substratesString = formatReactants(substrates);
        String productsString = formatReactants(products);
        return "$k_{1} \\cdot " + substratesString + " - k_{-1} \\cdot " + productsString + "$";
    }

    private static String formatIrreversibleKinetics(List<Reactant> substrates) {
        String substratesString = formatReactants(substrates);
        return "$k_{1} \\cdot " + substratesString + "$";
    }

    private static String formatMichaelisMentenKinetics(String substrate, String enzyme) {
        return String.format(michaelisMentenTemplate, enzyme, substrate, substrate);
    }

    private static String formatReactants(Collection<Reactant> reactants) {
        return reactants.stream().map(FormatReactionKinetics::formatSectionReactant)
                .collect(Collectors.joining(" \\cdot "));
    }

    private static String formatSectionReactant(Reactant reactant) {
        return "[\\text{" + reactant.getEntity().getIdentifier() + "}]";
    }


}
