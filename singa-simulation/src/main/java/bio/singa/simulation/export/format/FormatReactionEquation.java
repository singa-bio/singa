package bio.singa.simulation.export.format;

import bio.singa.chemistry.entities.ComplexModification;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.DynamicChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.DynamicReactantBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.StaticReactantBehavior;
import bio.singa.simulation.model.sections.CellTopology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    private static String formatSectionReactantsTex(Collection<Reactant> reactants, String delimiter) {
        return reactants.stream()
                .map(FormatReactionEquation::formatSectionReactantTex)
                .collect(Collectors.joining(delimiter + " "));
    }

    private static String formatSectionReactantTex(Reactant reactant) {
        String topology = mapTopologyToString(reactant.getPreferredTopology());
        return (reactant.getStoichiometricNumber() > 1 ? " " + (int) reactant.getStoichiometricNumber() + " " : "") +
                reactant.getEntity().getIdentifier().replaceAll("(\\d)", " $1 ") + "$_" + topology + "$";
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

    private static String formatDynamicSubstrates(Collection<DynamicChemicalEntity> substrates) {
        return substrates.stream()
                .map(FormatReactionEquation::formatDynamicSubstrate)
                .collect(Collectors.joining(" + "));
    }

    private static String formatDynamicSubstrate(DynamicChemicalEntity substrate) {
        String topologies = substrate.getPossibleTopologies().stream()
                .map(FormatReactionEquation::mapTopologyToString)
                .collect(Collectors.joining(","));
        return "[" + substrate.getIdentifier() + "]" + "(" + topologies + ")";
    }

    private static String mapTopologyToString(CellTopology topology) {
        switch (topology) {
            case INNER:
                return "i";
            case OUTER:
                return "o";
            default:
                return "m";
        }
    }

    private static String formatDynamicProducts(Map<String, List<ComplexModification>> dynamicProducts) {
        List<String> results = new ArrayList<>();
        for (Map.Entry<String, List<ComplexModification>> entry : dynamicProducts.entrySet()) {
            results.add("["+entry.getKey() + formatComplexModificaitions(entry.getValue()) + "]");
        }
        return String.join(" + ", results);
    }

    private static String formatComplexModificaitions(List<ComplexModification> complexModifications) {
        return complexModifications.stream()
                .map(FormatReactionEquation::mapComplexModificationToString)
                .collect(Collectors.joining(" "));
    }

    private static String mapComplexModificationToString(ComplexModification complexModification) {
        if (complexModification.getOperation().getText().equals(ComplexModification.Operation.SPLIT.getText())) {
            return ComplexModification.Operation.SPLIT.getText();
        }
        return complexModification.getOperation().getText() + complexModification.getModificator().getIdentifier();
    }

    public static String formatTex(Reaction reaction) {

        String substrates = "";
        String products = "";
        String catalysts = " ";
        if (reaction.getReactantBehavior() instanceof StaticReactantBehavior) {
            substrates = formatSectionReactantsTex(reaction.getReactantBehavior().getSubstrates(), " +");
            products = formatSectionReactantsTex(reaction.getReactantBehavior().getProducts(), " +");
            if (reaction.getReactantBehavior().getCatalysts().size() > 0) {
                catalysts = String.format(caralystFormatting, formatSectionReactantsTex(reaction.getReactantBehavior().getCatalysts(), ","));
            }
        }

        String arrow;
        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
            arrow = reversibleArrow;
        } else {
            arrow = irreversibleArrow;
        }

        return texPrefix + substrates + " " + arrow + " " + catalysts + products + texSuffix;
    }

    public static String formatASCII(Reaction reaction) {

        String substrates = formatSectionReactantsASCII(reaction.getReactantBehavior().getSubstrates(), " +");
        String products = formatSectionReactantsASCII(reaction.getReactantBehavior().getProducts(), " +");
        String catalysts = formatSectionReactantsASCII(reaction.getReactantBehavior().getCatalysts(), ",");

        if (reaction.getReactantBehavior() instanceof DynamicReactantBehavior) {
            DynamicReactantBehavior reactantBehaviour = (DynamicReactantBehavior) reaction.getReactantBehavior();
            if (substrates.equals("")) {
                substrates = formatDynamicSubstrates(reactantBehaviour.getDynamicSubstrates());
            } else {
                substrates += " + " + formatDynamicSubstrates(reactantBehaviour.getDynamicSubstrates());
            }
            if (products.equals("")) {
                products = formatDynamicProducts(reactantBehaviour.getDynamicProducts());
            } else {
                products += " + " + formatDynamicProducts(reactantBehaviour.getDynamicProducts());
            }
            if (products.isEmpty() && reactantBehaviour.isDynamicComplex()) {
                products = "[complex]";
            }
        }

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
}
