package bio.singa.simulation.export.format;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.*;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.RuleBasedReactantBehavior;
import bio.singa.simulation.model.sections.CellTopology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class FormatReactionEquation {

    private static String texPrefix = "\\ch{";
    private static String texSuffix = "}";

    private static String reversibleArrow = "<=>";
    private static String irreversibleArrow = "->";

    private static String caralystFormatting = "[ %s ] ";

    private static String michaelisMentenASCIITemplate = "%s - %s -> %s}";
    private static String irreversibleASCIITemplate = "%s -> %s";
    private static String reversibleASCIITemplate = "%s <=> %s";

    public static List<String> formatTex(Reaction reaction) {
        List<String> resultingStrings = new ArrayList<>();
        boolean isReversible = reaction.getKineticLaw() instanceof ReversibleKineticLaw;
        if (reaction.getReactantBehavior() instanceof RuleBasedReactantBehavior) {
            List<ReactantSet> reactantSets = reaction.getReactantBehavior().getReactantSets();
            for (ReactantSet reactantSet : reactantSets) {
                resultingStrings.add(formatReactantSet(reactantSet, isReversible));
            }
        } else {
            resultingStrings.add(formatReactantSet(reaction.getReactantBehavior().getSubstrates(),
                    reaction.getReactantBehavior().getProducts(),
                    reaction.getReactantBehavior().getCatalysts(),
                    isReversible));
        }
        return resultingStrings;
    }

    private static String formatReactantSet(ReactantSet reactantSet, boolean isReversible) {
        return formatReactantSet(reactantSet.getSubstrates(), reactantSet.getProducts(), reactantSet.getCatalysts(), isReversible);
    }

    private static String formatReactantSet(List<Reactant> substrates, List<Reactant> products, List<Reactant> catalysts, boolean isReversible) {
        String substratesString = formatReactantsTex(substrates, " +");
        String productsString = formatReactantsTex(products, " +");
        String catalystsString = "";
        if (catalysts.size() > 0) {
            catalystsString = String.format(caralystFormatting, formatReactantsTex(catalysts, ","));
        }
        String arrow = isReversible ? reversibleArrow : irreversibleArrow;
        return texPrefix + substratesString + " " + arrow + " " + catalystsString + productsString + texSuffix;
    }


    private static String formatReactantsTex(Collection<Reactant> reactants, String delimiter) {
        return reactants.stream()
                .map(FormatReactionEquation::formatReactantTex)
                .collect(Collectors.joining(delimiter + " "));
    }

    private static String formatReactantTex(Reactant reactant) {
        String stoichiometicNumber = reactant.getStoichiometricNumber() > 1
                ? " " + (int) reactant.getStoichiometricNumber() + " "
                : "";
        String entity = reactant.getEntity().getIdentifier().replaceAll("(\\d)", " $1 ");
        return stoichiometicNumber +  entity ;
    }

    private static String mapTopologyToString(CellTopology topology) {
        switch (topology) {
            case INNER:
                return "inner";
            case OUTER:
                return "outer";
            default:
                return "membrane";
        }
    }

    public static String formatASCII(List<Reactant> substrates, List<Reactant> products, List<Reactant> catalysts, KineticLaw law) {

        String substrateString = formatSectionReactantsASCII(substrates, " +");
        String productString = formatSectionReactantsASCII(products, " +");
        String catalystString = formatSectionReactantsASCII(catalysts, ",");

        if (law instanceof ReversibleKineticLaw) {
            return String.format(reversibleASCIITemplate, substrateString, productString);
        } else if (law instanceof IrreversibleKineticLaw) {
            return String.format(irreversibleASCIITemplate, substrateString, productString);
        } else if (law instanceof MichaelisMentenKineticLaw) {
            return String.format(michaelisMentenASCIITemplate, substrateString, catalystString, productString);
        } else if (law instanceof DynamicKineticLaw) {
            return String.format(michaelisMentenASCIITemplate, substrateString, catalystString, productString);
        } else {
            throw new IllegalArgumentException("The kinetic law " + law.getClass() + " has no implemented ASCII representation.");
        }
    }

    public static String formatASCII(Reaction reaction) {
        return formatASCII(reaction.getReactantBehavior().getSubstrates(), reaction.getReactantBehavior().getProducts(), reaction.getReactantBehavior().getCatalysts(), reaction.getKineticLaw());
    }

    private static String formatSectionReactantsASCII(Collection<Reactant> reactants, String delimiter) {
        return reactants.stream()
                .map(FormatReactionEquation::formatSectionReactantASCII)
                .collect(Collectors.joining(delimiter + " "));
    }

    private static String formatSectionReactantASCII(Reactant reactant) {
        String topologies = mapTopologyToString(reactant.getPreferredTopology());
        return (reactant.getStoichiometricNumber() > 1 ? (int) reactant.getStoichiometricNumber() : "") +
                reactant.getEntity().getIdentifier() + "(" + topologies + ")";
    }

}
