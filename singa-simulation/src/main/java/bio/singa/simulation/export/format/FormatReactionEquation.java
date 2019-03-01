package bio.singa.simulation.export.format;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.StaticReactantBehavior;

import java.util.Collection;
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
        return (reactant.getStoichiometricNumber() > 1 ? " " + (int) reactant.getStoichiometricNumber() + " " : "") +
                reactant.getEntity().getIdentifier().getContent().replaceAll("(\\d)", " $1 ") + "$_" + section + "$";
    }

    private static String formatSectionReactantsASCII(Collection<Reactant> reactants, String delimiter) {
        return reactants.stream()
                .map(FormatReactionEquation::formatSectionReactantASCII)
                .collect(Collectors.joining(delimiter + " "));
    }

    private static String formatSectionReactantASCII(Reactant reactant) {
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
        return (reactant.getStoichiometricNumber() > 1 ? (int) reactant.getStoichiometricNumber() : "") +
                reactant.getEntity().getIdentifier().getContent() + "(" + section + ")";
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

        String arrow = "";
        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
            arrow = reversibleArrow;
        } else {
            arrow = irreversibleArrow;
        }

        return texPrefix + substrates +" " + arrow +" "+ catalysts + products + texSuffix;
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
            return String.format(michaelisMentenASCIITemplate, substrates, products);
        } else if (reaction.getKineticLaw() instanceof DynamicKineticLaw) {
            return String.format(michaelisMentenASCIITemplate, substrates, catalysts, products);
        } else {
            throw new IllegalArgumentException("The kinetic law " + reaction.getKineticLaw().getClass() + " has no implemented ASCII representation.");
        }
    }

//    public static String formatTex(MichaelisMentenReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        String enzyme = reaction.getEnzyme().getIdentifier().getContent();
//        return String.format(michaelisMentenTexTemplate, substrates, enzyme, products);
//    }

//    public static String formatTex(NthOrderReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        return String.format(nthOrderTexTemplate, substrates, products);
//    }

//    public static String formatTex(ReversibleReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        return String.format(reversibleTexTemplate, substrates, products);
//    }

//    public static String formatTex(SectionDependentReaction reaction) {
//        String substrates = formatSectionReactantsTex(reaction.getSubstrates().stream());
//        String products = formatSectionReactantsTex(reaction.getProducts().stream());
//        return String.format(reversibleTexTemplate, substrates, products);
//    }

//    public static String formatTex(DynamicReaction reaction) {
//        String substrates = formatSectionReactantsTex(reaction.getSubstrates().stream());
//        String products = formatSectionReactantsTex(reaction.getProducts().stream());
//        String catalysts = formatSectionReactantsTex(reaction.getCatalysts().stream()).replace(" +", ",");
//        // escape number with [space]'number'[space]
//        return String.format(michaelisMentenTexTemplate, substrates, catalysts, products);
//    }

}
