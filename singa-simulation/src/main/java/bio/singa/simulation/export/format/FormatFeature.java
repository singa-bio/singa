package bio.singa.simulation.export.format;

import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class FormatFeature {

    public static List<String> formatAllEvidence(Feature feature) {
        List<String> evidences = new ArrayList<>();
        for (Object evidence : feature.getAllEvidence()) {
            String formattedEvidence = formatEvidence(evidence);
            if (!formattedEvidence.isEmpty()) {
                evidences.add(formattedEvidence);
            }
        }
        return evidences;
    }

    public static String formatEvidence(Object evidenceObject) {
        if (!(evidenceObject instanceof Evidence)) {
            throw new IllegalStateException("Failed to convert Evidence");
        }
        Evidence evidence = (Evidence) evidenceObject;
        if (evidence.equals(Evidence.NO_EVIDENCE)) {
            return "";
        }
        String evidenceString = "";
        if (evidence.getComment() != null) {
            evidenceString += evidence.getComment()+" ";
        }
        switch (evidence.getType()) {
            case DATABASE:
                evidenceString += "from database \\cite{" + evidence.getIdentifier().replace(" ", "") + "}";
                break;
            case LITERATURE:
                evidenceString += "from \\cite{" + evidence.getIdentifier().replace(" ", "") + "}";
                break;
            default:
                break;
        }
        return evidenceString;
    }

    /**
     * map: key: feature -> value: list: evidence
     *
     * @param reaction
     * @return
     */
    public static Map<String, List<String>> formatRates(Reaction reaction) {
        Map<String, List<String>> rates = new HashMap<>();
        if (reaction.getKineticLaw() instanceof ReversibleKineticLaw) {
            ReversibleKineticLaw kineticLaw = (ReversibleKineticLaw) reaction.getKineticLaw();

            ForwardsRateConstant kf = kineticLaw.getRate(ForwardsRateConstant.class);
            String kfString = "$k_1$ = " + kf.getContent();
            List<String> kfEvidence = formatAllEvidence(kf);
            rates.put(kfString, kfEvidence);

            BackwardsRateConstant kb = kineticLaw.getRate(BackwardsRateConstant.class);
            String kbString = "$k_{-1}$ = " + kb.getContent();
            List<String> kbEvidence = formatAllEvidence(kb);
            rates.put(kbString, kbEvidence);
        } else if (reaction.getKineticLaw() instanceof IrreversibleKineticLaw) {
            IrreversibleKineticLaw kineticLaw = (IrreversibleKineticLaw) reaction.getKineticLaw();

            ForwardsRateConstant kf = kineticLaw.getRate(ForwardsRateConstant.class);
            String kfString = "$k_1$ = " + kf.getContent();
            List<String> kfEvidence = formatAllEvidence(kf);
            rates.put(kfString, kfEvidence);
        } else if (reaction.getKineticLaw() instanceof MichaelisMentenKineticLaw) {
            MichaelisConstant km = reaction.getFeature(MichaelisConstant.class);
            String kmString = "$K_m$ = " + km.getContent();
            List<String> kmEvidence = formatAllEvidence(km);
            rates.put(kmString, kmEvidence);

            TurnoverNumber kCat = reaction.getFeature(TurnoverNumber.class);
            String kCatString = "$k_{cat}$ = " + kCat.getContent();
            List<String> kCatEvidence = formatAllEvidence(kCat);
            rates.put(kCatString, kCatEvidence);
        } else {
            return new HashMap<>();
        }
        return rates;
    }

    public static Map<String, List<String>> formatFeatures(UpdateModule module) {
        Map<String, List<String>> rates = new HashMap<>();
        for (Feature<?> feature : module.getFeatures()) {
            String kmString = feature.getDescriptor() + " = " + feature.getContent().toString().replace("_", "\\_");
            List<String> kmEvidence = formatAllEvidence(feature);
            rates.put(kmString, kmEvidence);
        }
        return rates;
    }


}
