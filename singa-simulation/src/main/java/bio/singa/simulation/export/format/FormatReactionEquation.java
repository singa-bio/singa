package bio.singa.simulation.export.format;

import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
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
            List<ReactantSet> reactantSets = reaction.getReactantBehavior().generateReactantSets(null);
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
        String catalystsString = " ";
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
        String topology = mapTopologyToString(reactant.getPreferredTopology());
        String stoichiometicNumber = reactant.getStoichiometricNumber() > 1
                ? " " + (int) reactant.getStoichiometricNumber() + " "
                : "";
        String entity = getEntityString(reactant);
        return stoichiometicNumber + "!(" + topology + ")(" + entity + ")";
    }

    static String getEntityString(Reactant reactant) {
        if (reactant.getEntity() instanceof ComplexEntity) {
            return ((ComplexEntity) reactant.getEntity()).getReferenceIdentifier();
        } else {
            return reactant.getEntity().getIdentifier();
        }
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

    public static String formatASCII(Reaction reaction) {

        String substrates = formatSectionReactantsASCII(reaction.getReactantBehavior().getSubstrates(), " +");
        String products = formatSectionReactantsASCII(reaction.getReactantBehavior().getProducts(), " +");
        String catalysts = formatSectionReactantsASCII(reaction.getReactantBehavior().getCatalysts(), ",");

        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
            return String.format(reversibleASCIITemplate, substrates, products);
        } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
            return String.format(irreversibleASCIITemplate, substrates, products);
        } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
            return String.format(michaelisMentenASCIITemplate, substrates, catalysts, products);
        } else if (reaction.getKineticLaw() instanceof DynamicKineticLaw) {
            return String.format(michaelisMentenASCIITemplate, substrates, catalysts, products);
        } else {
            throw new IllegalArgumentException("The kinetic law " + reaction.getKineticLaw().getClass() + " has no implemented ASCII representation.");
        }
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
