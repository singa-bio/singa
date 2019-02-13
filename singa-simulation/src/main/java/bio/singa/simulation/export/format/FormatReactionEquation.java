package bio.singa.simulation.export.format;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class FormatReactionEquation {

    private static String michaelisMentenTexTemplate = "\\ch{%s ->[ %s ] %s}";
    private static String nthOrderTexTemplate = "\\ch{%s -> %s}";
    private static String reversibleTexTemplate = "\\ch{%s <=> %s}";
    private static String complexTexTemplate = "\\ch{%s + %s <=> %s}";

    private static String michaelisMentenASCIITemplate = "%s - %s -> %s}";
    private static String nthOrderASCIITemplate = "%s -> %s";
    private static String reversibleASCIITemplate = "%s <=> %s";
    private static String complexASCIITemplate = "%s + %s <=> %s";

    private static String formatReactants(Stream<Reactant> reactants) {
        return reactants.map(FormatReactionEquation::formatReactant)
                .collect(Collectors.joining(" + "));
    }

    private static String formatReactant(Reactant reactant) {
        return (reactant.getStoichiometricNumber() > 1 ? " "+(int) reactant.getStoichiometricNumber()+" " : "") +
                reactant.getEntity().getIdentifier().getContent().replaceAll("(\\d)", " $1 ");
    }

    private static String formatSectionReactantsTex(Stream<Reactant> reactants) {
        return reactants.map(FormatReactionEquation::formatSectionReactantTex)
                .collect(Collectors.joining(" + "));
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
        return (reactant.getStoichiometricNumber() > 1 ? " "+(int) reactant.getStoichiometricNumber()+" " : "") +
                reactant.getEntity().getIdentifier().getContent().replaceAll("(\\d)", " $1 ") + "$_" + section + "$";
    }

    private static String formatSectionReactantsASCII(Stream<Reactant> reactants) {
        return reactants.map(FormatReactionEquation::formatSectionReactantASCII)
                .collect(Collectors.joining(" + "));
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

//        throw new IllegalArgumentException("The reaction class " + reaction.getClass() + " lacks an implemented latex format.");
        return "";
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

//    public static String formatASCII(MichaelisMentenReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        String enzyme = reaction.getEnzyme().getIdentifier().getContent();
//        return String.format(michaelisMentenASCIITemplate, substrates, enzyme, products);
//    }

//    public static String formatASCII(NthOrderReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        return String.format(nthOrderASCIITemplate, substrates, products);
//    }

//    public static String formatASCII(ReversibleReaction reaction) {
//        String substrates = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isSubstrate));
//        String products = formatReactants(reaction.getStoichiometricReactants().stream().filter(Reactant::isProduct));
//        return String.format(reversibleASCIITemplate, substrates, products);
//    }

//    public static String formatASCII(SectionDependentReaction reaction) {
//        String substrates = formatSectionReactantsASCII(reaction.getSubstrates().stream());
//        String products = formatSectionReactantsASCII(reaction.getProducts().stream());
//        return String.format(reversibleASCIITemplate, substrates, products);
//    }

//    public static String formatASCII(DynamicReaction reaction) {
//        String substrates = formatSectionReactantsASCII(reaction.getSubstrates().stream());
//        String products = formatSectionReactantsASCII(reaction.getProducts().stream());
//        String catalysts = formatSectionReactantsASCII(reaction.getCatalysts().stream()).replace(" +", ",");
//        return String.format(michaelisMentenTexTemplate, substrates, catalysts, products);
//    }

}
