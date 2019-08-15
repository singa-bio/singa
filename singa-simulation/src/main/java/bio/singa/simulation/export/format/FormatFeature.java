package bio.singa.simulation.export.format;

import bio.singa.chemistry.features.reactions.*;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class FormatFeature {

    private static String getPrefix(RateConstant rate) {
        if (rate instanceof ForwardsRateConstant) {
            return "";
        } else if (rate instanceof BackwardsRateConstant) {
            return "-";
        } else {
            return ";";
        }
    }

    public static String formatFeatures(List<Feature> features) {
        List<String> featureStrings = new ArrayList<>();
        for (Feature feature : features) {
            if (feature instanceof RateConstant) {
                featureStrings.add(formatRate(((RateConstant) feature)));
            }
        }

        return String.join(", ", featureStrings);
    }

    public static String formatRate(RateConstant rate) {
        return "k$_{" + FormatFeature.getPrefix(rate) + "1}$ = " + rate.getContent();
    }

    public static String formatEvidence(Evidence evidence) {
        switch (evidence.getType()) {
            case GUESS:
            case ESTIMATION:
            case PREDICTION:
                return "estimated";
            case DATABASE:
                return "from database \\cite{" + evidence.getIdentifier().replace(" ", "") + "}";
            case LITERATURE:
                return "from \\cite{" + evidence.getIdentifier().replace(" ", "") + "}";
            default:
                return "unsupported evidence type";
        }
    }

    public static List<String> formatRates(Reaction reaction) {
        List<String> rates = new ArrayList<>();
        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
            ReversibleKineticLaw kineticLaw = (ReversibleKineticLaw) reaction.getKineticLaw();
            ForwardsRateConstant kf = kineticLaw.getRate(ForwardsRateConstant.class);
            rates.add("$k_1$ = " + kf.getContent() + " " + formatEvidence(kf.getPrimaryEvidence()));
            BackwardsRateConstant kb = kineticLaw.getRate(BackwardsRateConstant.class);
            rates.add("$k_{-1}$ = " + kb.getContent() + " " + formatEvidence(kb.getPrimaryEvidence()));
        } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
            IrreversibleKineticLaw kineticLaw = (IrreversibleKineticLaw) reaction.getKineticLaw();
            ForwardsRateConstant kf = kineticLaw.getRate(ForwardsRateConstant.class);
            rates.add("$k_1$ = " + kf.getContent() + " " + formatEvidence(kf.getPrimaryEvidence()));
        } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
            MichaelisConstant km = reaction.getFeature(MichaelisConstant.class);
            rates.add("$K_m$ = " + km.getContent() + " " + formatEvidence(km.getPrimaryEvidence()));
            TurnoverNumber kCat = reaction.getFeature(TurnoverNumber.class);
            rates.add("$k_{cat}$ = " + kCat.getContent() + " " + formatEvidence(kCat.getPrimaryEvidence()));
        } else {
            return Collections.emptyList();
        }
        return rates;
    }


}
